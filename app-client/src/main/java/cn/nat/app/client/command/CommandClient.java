package cn.nat.app.client.command;

import cn.nat.app.client.command.handlers.ChannelRequestFrameHandler;
import cn.nat.app.client.command.handlers.ServerEndpointFrameHandler;
import cn.nat.app.client.config.ClientConfig;
import cn.nat.app.client.data.ProxyTunnel;
import cn.nat.common.container.ConfigurableContainerSupport;
import cn.nat.common.container.Resource;
import cn.nat.common.data.ClientStartupFrame;
import cn.nat.common.netty.NettyInitializer;
import cn.nat.common.protocol.Frame;
import cn.nat.common.protocol.FrameHandlerRegistry;
import cn.nat.common.protocol.StreamFrameDispatcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
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
        CommandClient _this = this;
        initializeHandlers(context, config);

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = NettyInitializer.createBootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
        bootstrap.handler(new StreamFrameDispatcher(registry));

        ClientConfig.Server server = config.getServer();

        bootstrap.connect(server.getHost(), server.getPort()).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                List<String> tunnels = config.getTunnels().stream().map(ClientConfig.Tunnel::getName).toList();
                Frame frame = new ClientStartupFrame().clientId(config.getClientId()).tunnels(tunnels).createFrame();
                future.channel().writeAndFlush(frame.serialize()).addListener(started -> {
                    if (!started.isSuccess()) {
                        context.stopAndRemove(_this);
                    }
                });
            } else {
                context.stopAndRemove(_this);
            }
        });

        return List.of(eventLoopGroup::shutdownGracefully);
    }

    @Override
    protected String print(ClientConfig config) {
        ClientConfig.Server server = config.getServer();
        return "Command Client connect to %s:%d.".formatted(server.getHost(), server.getPort());
    }

    private void initializeHandlers(Context context, ClientConfig config) {
        List<ClientConfig.Tunnel> tunnelConfigs = config.getTunnels();
        List<ProxyTunnel> tunnels = new ArrayList<>(tunnelConfigs.size());
        for (ClientConfig.Tunnel tunnelConfig : tunnelConfigs) {
            // @formatter:off
            ProxyTunnel tunnel = new ProxyTunnel()
                    .name(tunnelConfig.getName())
                    .host(tunnelConfig.getHost())
                    .port(tunnelConfig.getPort());
            // @formatter:on
            tunnels.add(tunnel);
        }
        registry.register(new ServerEndpointFrameHandler(config.getServer().getHost()));
        registry.register(new ChannelRequestFrameHandler(tunnels));
    }
}
