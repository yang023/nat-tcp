package cn.nat.app.client.command.proxy;

import cn.nat.app.client.data.ProxyTunnel;
import cn.nat.common.container.ConfigurableContainerSupport;
import cn.nat.common.container.Resource;
import cn.nat.common.data.ConnectFrame;
import cn.nat.common.netty.NettyInitializer;
import cn.nat.common.protocol.Frame;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Collection;
import java.util.List;

/**
 * @author yang
 */
public class ProxyClient extends ConfigurableContainerSupport<ProxyTunnel, ProxyClient> {
    private final ProxyFactory factory;
    private final String requestId;

    public ProxyClient(String requestId) {
        this.factory = ProxyFactory.instance;
        this.requestId = requestId;
    }

    @Override
    protected Collection<Resource> start(Context context, ProxyTunnel tunnel) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = NettyInitializer.createBootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProxyClientHandler(tunnel));
                NettyInitializer.withTrafficShaping(ch, factory.readRate, factory.writeRate);
            }
        });

        bootstrap.connect(factory.streamHost, factory.streamPort).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                Frame frame = new ConnectFrame().tunnel(tunnel.name()).requestId(requestId).createFrame();
                future.channel().writeAndFlush(frame.serialize());
            } else {
                stop();
            }
        });

        return List.of(eventLoopGroup::shutdownGracefully);
    }

    @Override
    protected String print(ProxyTunnel tunnel) {
        return null;
    }
}
