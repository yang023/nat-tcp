package cn.slackoff.nat.app.client.handlers;

import cn.slackoff.nat.app.client.components.context.ClientContext;
import cn.slackoff.nat.app.client.components.context.ClientContextHolder;
import cn.slackoff.nat.core.boot.CustomizeNettyClient;
import cn.slackoff.nat.core.data.ErrorResponse;
import cn.slackoff.nat.core.execusion.FrameHandler;
import cn.slackoff.nat.core.protocol.*;
import cn.slackoff.nat.core.tools.OriginalBytesHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * 接受报文转发流请求<br>
 * TODO 暂只实现 http 穿透，后续兼容其他 tcp 协议（maybe）穿透
 *
 * @author yang
 */
public class StreamRequestHandler implements FrameHandler {
    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        String endpoint = input.headers().get("endpoint");
        ByteBuf stream = input.input();
        ClientContext context = ClientContextHolder.getContext();
        Optional<CustomizeNettyClient> clientOptional = context.getPreparedClient(endpoint);
        if (clientOptional.isEmpty()) {
            ErrorResponse.create(Command.STREAM_REQUEST).write(output);
            return;
        }
        Consumer<Headers> outputHeaders = h -> {
            // TODO 统一参数
            h.set("channelId", input.headers().get("channelId"));
            h.set("clientId", context.getClientId());
        };
        CustomizeNettyClient client = clientOptional.get();
        client.addConfigurer(ch -> {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    ctx.channel().writeAndFlush(stream);
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (!(msg instanceof FullHttpResponse)) {
                        super.channelRead(ctx, msg);
                        return;
                    }
                    ByteBuf responseBytes = OriginalBytesHolder.findChannelBytes(ctx.channel());
                    output.command(Command.STREAM_RESPONSE)
                          .contentType(ContentType.STREAM)
                          .headers(outputHeaders)
                          .write(responseBytes)
                          .addListener(e -> ctx.channel().close());
                }
            });
        });
        client.setErrorCallback(e -> {
            Throwable cause = e.cause();
            output.headers(outputHeaders);
            ErrorResponse.create(Command.STREAM_REQUEST, cause == null ? null : cause.getMessage()).write(output);
            e.channel().close();
        });
        client.connect(endpoint);
    }
}
