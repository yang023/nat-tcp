package cn.nat.app.server.proxy;

import cn.nat.app.server.command.CommandChannelMapping;
import cn.nat.common.data.ChannelRequestFrame;
import cn.nat.common.data.ErrorFrame;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yang
 */
final class ProxyGatewayHandler extends SimpleChannelInboundHandler<Object> {
    private static final AttributeKey<Channel> G_S_CHANNEL_KEY = AttributeKey.valueOf("g-s-channel");
    private static final AttributeKey<String> G_TUNNEL_KEY = AttributeKey.valueOf("gateway-tunnel");

    private final ProxyMessageHandler messageHandler;

    ProxyGatewayHandler(ProxyMessageHandler messageHandler) {
        if (messageHandler == null) {
            throw new IllegalStateException("未指定消息处理器");
        }
        this.messageHandler = messageHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel gatewayChannel = ctx.channel();
        if (messageHandler.isFirstRequest(msg)) {

            String tunnelKey = messageHandler.findTunnelKey(msg);
            if (!StringUtils.hasText(tunnelKey)) {
                ErrorFrame errorFrame = new ErrorFrame().code(500).message("未指定代理隧道");
                messageHandler.sendError(gatewayChannel, errorFrame);
                return;
            }
            String requestId = UUID.randomUUID().toString();
            if (!requestStreamConnecting(tunnelKey, requestId)) {
                ErrorFrame errorFrame = new ErrorFrame().code(500).message("未连接: %s".formatted(tunnelKey));
                messageHandler.sendError(gatewayChannel, errorFrame);
                return;
            }

            Channel stream = StreamChannelMapping.streamWithBindKey(tunnelKey, requestId, Duration.ofSeconds(30));

            if (stream == null) {
                ErrorFrame errorFrame = new ErrorFrame().code(500).message("未连接: %s".formatted(tunnelKey));
                messageHandler.sendError(gatewayChannel, errorFrame);
                return;
            }

            StreamChannelMapping.registerMessageListener(stream, income -> {
                if (messageHandler.isLastResponse(income)) {
                    gatewayChannel.writeAndFlush(income).addListener((ChannelFutureListener) f -> {
                        if (messageHandler.closeOnLastResponse()) {
                            f.channel().close();
                        } else {
                            f.channel().read();
                        }
                    });
                } else {
                    gatewayChannel.writeAndFlush(income);
                }
            });

            messageHandler.applyStreamCodec(stream.pipeline());

            map_G_S_channel(gatewayChannel, stream, tunnelKey);
            stream.writeAndFlush(msg);
        } else {
            Channel stream = findStream(gatewayChannel);
            stream.writeAndFlush(msg);
        }
    }

    private Channel findAvailableChannels(String tunnel, String requestId) {
        return null;
    }

    private boolean requestStreamConnecting(String tunnel, String requestId) {
        AtomicReference<Boolean> ref = new AtomicReference<>(null);
        ChannelRequestFrame request = new ChannelRequestFrame().requestId(requestId).tunnel(tunnel);
        ChannelFuture channelFuture = CommandChannelMapping.sendFrameTo(tunnel, request.createFrame());
        if (channelFuture == null) {
            return false;
        }
        channelFuture.addListener(f -> ref.set(f.isSuccess()));

        while (true) {
            if (ref.get() != null) {
                break;
            }
        }

        return ref.get();
    }

    private void map_G_S_channel(Channel gateway, Channel stream, String tunnel) {
        stream.attr(G_S_CHANNEL_KEY).set(gateway);
        stream.closeFuture().addListener(f -> {
            if (gateway.isActive()) {
                gateway.close();
            }
        });
        gateway.attr(G_S_CHANNEL_KEY).set(stream);
        gateway.closeFuture().addListener(f -> {
            if (stream.isActive()) {
                stream.close();
            }
        });
        gateway.attr(G_TUNNEL_KEY).set(tunnel);
    }

    private Channel findStream(Channel gateway) {
        return gateway.attr(G_S_CHANNEL_KEY).get();
    }
}
