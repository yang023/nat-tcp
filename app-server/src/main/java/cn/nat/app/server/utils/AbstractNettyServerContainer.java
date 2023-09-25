package cn.nat.app.server.utils;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.common.container.ConfigurableContainerSupport;
import cn.nat.common.container.Resource;
import cn.nat.common.netty.NettyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Collection;
import java.util.List;

/**
 * @author yang
 */
public abstract class AbstractNettyServerContainer<T extends AbstractNettyServerContainer<T>>
        extends ConfigurableContainerSupport<ServerConfig, T> {

    @Override
    protected Collection<Resource> start(Context context, ServerConfig config) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = NettyInitializer.createServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class).group(boss, worker);

        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                configure(context, ch);
            }
        });

        int port = resolvePort(config);

        bootstrap.bind(port);

        return List.of(boss::shutdownGracefully, worker::shutdownGracefully);
    }

    protected abstract int resolvePort(ServerConfig config);

    protected abstract void configure(Context context, NioSocketChannel channel);
}