package cn.nat.app.server.command;

import cn.nat.app.server.command.handlers.ClientStartupFrameHandler;
import cn.nat.app.server.config.ServerConfig;
import cn.nat.app.server.utils.AbstractNettyServerContainer;
import cn.nat.common.protocol.FrameHandlerRegistry;
import cn.nat.common.protocol.StreamFrameDispatcher;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yang
 */
@Component
public class CommandServer extends AbstractNettyServerContainer<ServerConfig, CommandServer> {

    private final FrameHandlerRegistry registry = new FrameHandlerRegistry();

    @Autowired
    @Override
    public CommandServer config(ServerConfig config) {
        return super.config(config);
    }

    @Override
    protected int resolvePort(ServerConfig config) {
        return config.getPort();
    }

    @Override
    protected void configure(Context context, ServerConfig config, NioSocketChannel channel) {
        initializeHandlers(config);

        channel.pipeline().addLast(new StreamFrameDispatcher(registry));
    }

    @Override
    protected String print(ServerConfig config) {
        return "Command Server start on %d.".formatted(config.getPort());
    }

    private void initializeHandlers(ServerConfig config) {
        registry.register(new ClientStartupFrameHandler(config, CommandChannelMapping::add));
    }
}
