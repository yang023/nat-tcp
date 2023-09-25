package cn.nat.app.server.command;

import cn.nat.app.server.command.handlers.ClientStartupFrameHandler;
import cn.nat.app.server.config.ServerConfig;
import cn.nat.common.container.ConfigurableContainerSupport;
import cn.nat.common.container.Resource;
import cn.nat.common.netty.NettyInitializer;
import cn.nat.common.protocol.DatagramFrameDispatcher;
import cn.nat.common.protocol.FrameHandlerRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yang
 */
@Component
public class CommandServer extends ConfigurableContainerSupport<ServerConfig, CommandServer> {

    private final FrameHandlerRegistry registry = new FrameHandlerRegistry();

    @Autowired
    @Override
    public CommandServer config(ServerConfig config) {
        return super.config(config);
    }

    @Override
    protected Collection<Resource> start(Context context, ServerConfig config) {
        initializeHandlers(config);

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = NettyInitializer.createBootstrap();
        bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class);
        bootstrap.handler(new DatagramFrameDispatcher(registry));

        bootstrap.bind(config.getPort());

        return Collections.singletonList(eventLoopGroup::shutdownGracefully);
    }

    @Override
    protected String print(ServerConfig config) {
        return "Command Server start on %d.".formatted(config.getPort());
    }

    private void initializeHandlers(ServerConfig config) {
        registry.register(new ClientStartupFrameHandler(config));
    }
}
