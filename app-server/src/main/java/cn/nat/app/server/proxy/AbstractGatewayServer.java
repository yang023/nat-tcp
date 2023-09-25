package cn.nat.app.server.proxy;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.app.server.utils.AbstractNettyServerContainer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yang
 */
public abstract class AbstractGatewayServer<T extends AbstractGatewayServer<T>> extends AbstractNettyServerContainer<T> {
    private static final AttributeKey<Channel> GATEWAY_STREAM_KEY = AttributeKey.valueOf("gateway-stream");
    private static final UnbindChannelMatcher UNBIND_MATCHER = new UnbindChannelMatcher();

    private final ChannelSelector selector;
    private final String protocol;

    protected AbstractGatewayServer(String protocol) {
        this.selector = ChannelSelectors.getDefault();
        this.protocol = protocol;
    }

    protected String protocol() {
        return this.protocol;
    }

    @Override
    protected void configure(Context context, NioSocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        gatewayDecoder(pipeline);
        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                Channel gatewayChannel = ctx.channel();
                if (isFirstMessage(msg)) {
                    String proxyName = resolveProxyName(msg);

                    ChannelGroup channels = context.getContainer(ProxyStreamServer.class).findChannelGroup(proxyName);
                    bindCodec(channels);

                    // 选出一个
                    Optional<Channel> select;
                    synchronized (ChannelSelectors.class) {
                        Set<Channel> collect = channels.stream().filter(UNBIND_MATCHER::matches)
                                .collect(Collectors.toSet());
                        select = selector.select(collect);
                        select.ifPresent(ch -> {
                            cacheStreamId(gatewayChannel, ch);
                            ProxyChannelMapping.bind(gatewayChannel, ch, stream -> {
                                onStreamMessage(gatewayChannel, stream);
                            });
                        });
                    }

                    if (select.isPresent()) {
                        select.get().writeAndFlush(msg);
                    } else {
                        sendChannelFoundError(gatewayChannel, msg);
                    }
                } else {
                    Channel streamChannel = loadStreamId(gatewayChannel);

                    if (streamChannel != null) {
                        streamChannel.writeAndFlush(msg);
                    } else {
                        sendChannelFoundError(gatewayChannel, msg);
                    }
                }
            }
        });
    }

    @Override
    protected int resolvePort(ServerConfig config) {
        ServerConfig.Gateway proxy = config.getGateway(protocol);
        return proxy.getPort();
    }

    @Override
    protected String print(ServerConfig config) {
        ServerConfig.Gateway proxy = config.getGateway(protocol);
        return print(proxy);
    }

    private void onStreamMessage(Channel gateway, Object msg) {
        gateway.writeAndFlush(msg).addListener(f -> {
            gateway.read();
        });
    }

    protected abstract String print(ServerConfig.Gateway proxy);

    protected abstract boolean isFirstMessage(Object msg);

    protected abstract void gatewayDecoder(ChannelPipeline pipeline);

    protected abstract void bindCodec(ChannelGroup channels);

    protected abstract String resolveProxyName(Object msg);

    protected abstract void sendChannelFoundError(Channel gateway, Object msg);

    private static void cacheStreamId(Channel gateway, Channel stream) {
        synchronized (GATEWAY_STREAM_KEY) {
            Attribute<Channel> attr = gateway.attr(GATEWAY_STREAM_KEY);
            attr.set(stream);
        }
    }

    private static Channel loadStreamId(Channel gateway) {
        synchronized (GATEWAY_STREAM_KEY) {
            Attribute<Channel> attr = gateway.attr(GATEWAY_STREAM_KEY);
            return attr.get();
        }
    }

    static class UnbindChannelMatcher implements ChannelMatcher {

        @Override
        public boolean matches(Channel channel) {
            return !ProxyChannelMapping.isBound(channel);
        }
    }
}