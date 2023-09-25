package cn.nat.app.server.proxy.protocol;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.app.server.proxy.AbstractGatewayServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author yang
 */
@Component
public final class HttpGateway extends AbstractGatewayServer<HttpGateway> {

    public HttpGateway() {
        super("http");
    }

    @Autowired
    @Override
    public HttpGateway config(ServerConfig config) {
        return super.config(config);
    }

    @Override
    protected void checkIfPreset(ServerConfig config) {
        ServerConfig.Gateway gateway = config.getGateway(this.protocol());
        gateway.setPort(gateway.getPort(8080));
    }

    @Override
    protected String print(ServerConfig.Gateway gateway) {
        return "Http Gateway Proxy Service %d".formatted(gateway.getPort());
    }

    @Override
    protected void gatewayDecoder(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
    }

    @Override
    protected void bindCodec(ChannelGroup channels) {
        for (Channel channel : channels) {
            ChannelPipeline pipeline = channel.pipeline();
            if (pipeline.get(HttpRequestEncoder.class) == null) {
                pipeline.addFirst(new HttpRequestEncoder());
            }
            if (pipeline.get(HttpResponseDecoder.class) == null) {
                pipeline.addFirst(new HttpResponseDecoder());
            }
        }
    }

    @Override
    protected String resolveProxyName(Object msg) {
        return "test";
    }

    @Override
    protected boolean isFirstMessage(Object msg) {
        return msg instanceof HttpRequest;
    }

    @Override
    protected void sendChannelFoundError(Channel gateway, Object msg) {
        if (msg instanceof LastHttpContent) {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

            ByteBuf content = response.content();
            // TODO 找个页面展示
            content.writeCharSequence("未注册代理通道", StandardCharsets.UTF_8);

            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");

            gateway.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}