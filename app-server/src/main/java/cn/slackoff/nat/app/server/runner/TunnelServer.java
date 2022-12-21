package cn.slackoff.nat.app.server.runner;

import cn.slackoff.nat.app.server.components.client.ClientRepository;
import cn.slackoff.nat.app.server.handlers.ConnectRequestHandler;
import cn.slackoff.nat.app.server.handlers.ErrorResponseHandler;
import cn.slackoff.nat.app.server.handlers.PrepareResponseHandler;
import cn.slackoff.nat.app.server.handlers.StreamResponseHandler;
import cn.slackoff.nat.core.boot.AbstractNettyServer;
import cn.slackoff.nat.core.execusion.FrameChannelHandler;
import cn.slackoff.nat.core.execusion.FrameCodec;
import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.matcher.FrameMatchers;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yang
 */
@Slf4j
public class TunnelServer extends AbstractNettyServer {
    @Setter
    private ClientRepository clientRepository;

    @Override
    protected void onError(ChannelFuture channelFuture) {
        log.error("Server cannot run.", channelFuture.cause());
        System.exit(-1);
    }

    @Override
    protected void configServer(ServerBootstrap bootstrap) {
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast(new FrameCodec());
                pipeline.addLast(new FrameChannelHandler(map -> {
                    ConnectRequestHandler connectRequestHandler = new ConnectRequestHandler();
                    connectRequestHandler.setClientRepository(clientRepository);
                    map.put(FrameMatchers.ofCommand(Command.CONNECT_REQUEST), connectRequestHandler);

                    map.put(FrameMatchers.ofCommand(Command.PREPARE_RESPONSE), new PrepareResponseHandler());

                    map.put(FrameMatchers.ofCommand(Command.STREAM_RESPONSE), new StreamResponseHandler());

                    map.put(FrameMatchers.ofCommand(Command.ERROR_RESPONSE), new ErrorResponseHandler());
                }));
            }
        });
    }
}
