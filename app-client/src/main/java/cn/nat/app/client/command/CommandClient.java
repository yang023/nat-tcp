package cn.nat.app.client.command;

import cn.nat.app.client.command.handlers.ServerEndpointFrameHandler;
import cn.nat.app.client.config.ClientConfig;
import cn.nat.app.client.data.ProxyTunnel;
import cn.nat.common.container.ConfigurableContainerSupport;
import cn.nat.common.container.Resource;
import cn.nat.common.netty.NettyInitializer;
import cn.nat.common.protocol.DatagramFrameDispatcher;
import cn.nat.common.protocol.FrameHandlerRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author yang
 */
@Component
public class CommandClient extends ConfigurableContainerSupport<ClientConfig, CommandClient> {

    private final FrameHandlerRegistry registry = new FrameHandlerRegistry();

    @Autowired
    @Override
    public CommandClient config(ClientConfig config) {
        return super.config(config);
    }

    @Override
    protected Collection<Resource> start(Context context, ClientConfig config) {
        initializeHandlers(context, config);

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = NettyInitializer.createBootstrap();
        bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class);
        bootstrap.handler(new DatagramFrameDispatcher(registry));

        bootstrap.bind(0).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                new Startup().config(config).execute(future);
            }
        });

        return Collections.singletonList(eventLoopGroup::shutdownGracefully);
    }

    @Override
    protected String print(ClientConfig config) {
        ClientConfig.Server server = config.getServer();
        return "Command Client connect to %s:%d.".formatted(server.getHost(), server.getPort());
    }

    private void initializeHandlers(Context context, ClientConfig config) {
        // TODO 配置
        ProxyTunnel tunnel = new ProxyTunnel().name("test").host("localhost").port(80);
        List<ProxyTunnel> tunnels = List.of(tunnel);
        registry.register(new ServerEndpointFrameHandler(context, config, tunnels));
    }
}
