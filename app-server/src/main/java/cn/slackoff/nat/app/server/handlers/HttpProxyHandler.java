package cn.slackoff.nat.app.server.handlers;

import cn.slackoff.nat.app.server.components.registration.Registration;
import cn.slackoff.nat.app.server.components.registration.RegistrationManager;
import cn.slackoff.nat.core.data.PrepareRequest;
import cn.slackoff.nat.core.data.TunnelInfo;
import cn.slackoff.nat.core.execusion.impl.DefaultFrameOutput;
import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.ContentType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author yang
 */
public class HttpProxyHandler extends ChannelInboundHandlerAdapter {
    @Setter
    private String proxyHeader;

    private static void sendError(ChannelHandlerContext ctx, HttpVersion version, HttpResponseStatus status) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(version, status);
        sendError(ctx, response);
    }

    private static void sendError(ChannelHandlerContext ctx, FullHttpResponse response) {
        ctx.pipeline().addFirst(new HttpServerCodec());
        ctx.channel().writeAndFlush(response)
           .addListeners(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest request)) {
            super.channelRead(ctx, msg);
            return;
        }

        String domain = request.headers().get(proxyHeader);
        if (!StringUtils.hasText(domain)) {
            sendError(ctx, request.protocolVersion(), HttpResponseStatus.BAD_REQUEST);
            return;
        }

        Optional<TunnelInfo> tunnelOptional = RegistrationManager.findTunnelByDomain(domain);
        Optional<Registration> registrationOptional = RegistrationManager.findRegistrationByDomain(domain);
        if (registrationOptional.isEmpty()) {
            sendError(ctx, request.protocolVersion(), HttpResponseStatus.UNAUTHORIZED);
            return;
        }
        if (tunnelOptional.isEmpty()) {
            sendError(ctx, request.protocolVersion(), HttpResponseStatus.NOT_FOUND);
            return;
        }

        Registration registration = registrationOptional.get();
        registration.addClientChannel(ctx.channel(), error -> {
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR);
            if (StringUtils.hasText(error.getMessage())) {
                httpResponse.content().writeCharSequence(error.getMessage(), StandardCharsets.UTF_8);
            }

            sendError(ctx, httpResponse);
        });

        TunnelInfo tunnel = tunnelOptional.get();

        PrepareRequest prepareRequest = new PrepareRequest();
        prepareRequest.setTunnel(tunnel);
        prepareRequest.setChannelId(ctx.channel().id().asLongText());
        DefaultFrameOutput output = new DefaultFrameOutput(registration.getServerChannel());
        output.command(Command.PREPARE_REQUEST)
              .contentType(ContentType.JSON)
              .write(prepareRequest);
    }
}
