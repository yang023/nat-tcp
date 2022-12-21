package cn.slackoff.nat.app.client.runner;

import cn.slackoff.nat.app.client.handlers.ConnectResponseHandler;
import cn.slackoff.nat.app.client.handlers.ErrorResponseClientHandler;
import cn.slackoff.nat.app.client.handlers.PrepareRequestHandler;
import cn.slackoff.nat.app.client.handlers.StreamRequestHandler;
import cn.slackoff.nat.core.boot.AbstractNettyClient;
import cn.slackoff.nat.core.execusion.FrameChannelHandler;
import cn.slackoff.nat.core.execusion.FrameCodec;
import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.matcher.FrameMatchers;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yang
 */
@Slf4j
public class TunnelClient extends AbstractNettyClient {
    @Override
    protected void configClient(Bootstrap bootstrap) {
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast(new FrameCodec());
                pipeline.addLast(new FrameChannelHandler(map -> {
                    map.put(FrameMatchers.ofCommand(Command.ERROR_RESPONSE), new ErrorResponseClientHandler());
                    map.put(FrameMatchers.ofCommand(Command.CONNECT_RESPONSE), new ConnectResponseHandler());
                    map.put(FrameMatchers.ofCommand(Command.PREPARE_REQUEST), new PrepareRequestHandler());
                    map.put(FrameMatchers.ofCommand(Command.STREAM_REQUEST), new StreamRequestHandler());
                }));
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                ctx.channel().closeFuture().addListener(e -> log.error("Disconnecting from server."));

                ctx.close();
            }
        });
    }

    @Override
    protected void onError(ChannelFuture channelFuture) {
        log.error("Client cannot connect to server.", channelFuture.cause());
        System.exit(-1);
    }
}
