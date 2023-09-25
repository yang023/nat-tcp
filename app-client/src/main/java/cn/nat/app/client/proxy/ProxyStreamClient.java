package cn.nat.app.client.proxy;

import cn.nat.common.container.ContainerSupport;
import cn.nat.common.container.Resource;
import cn.nat.common.data.ConnectFrame;
import cn.nat.common.netty.NettyInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Collection;
import java.util.Collections;

/**
 * 接收服务端推送的代理数据，进行转发到本地服务
 *
 * @author yang
 */
public final class ProxyStreamClient extends ContainerSupport<ProxyStreamClient> {

    private final String proxyName;
    private String connectHost;
    private int connectPort;

    private String proxyHost;
    private int proxyPort;

    private long readRate;
    private long writeRate;

    public ProxyStreamClient(String proxyName) {
        this.proxyName = proxyName;
    }

    public ProxyStreamClient connect(String host, int port) {
        this.connectHost = host;
        this.connectPort = port;
        return this;
    }

    public ProxyStreamClient proxyTo(String host, int port) {
        this.proxyHost = host;
        this.proxyPort = port;
        return this;
    }

    public ProxyStreamClient limit(long readRate, long writeRate) {
        this.readRate = readRate;
        this.writeRate = writeRate;
        return this;
    }

    @Override
    protected Collection<Resource> start0(Context context) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = NettyInitializer.createBootstrap();
        bootstrap.channel(NioSocketChannel.class).group(eventLoopGroup);

        bootstrap.handler(new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                InternalProxyClient client = InternalProxyClient.create(ctx.channel())
                        .proxy(proxyHost, proxyPort)
                        .limit(readRate, writeRate);
                client.start(null);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                InternalProxyClient client = InternalProxyClient.resolve(ctx.channel());
                client.send(msg);
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(connectHost, connectPort);

        channelFuture.addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                ConnectFrame frame = new ConnectFrame().tunnel(proxyName);
                f.channel().writeAndFlush(frame.createFrame().serialize());
            } else {
                f.cause().printStackTrace();
            }
        });

        return Collections.singletonList(eventLoopGroup::shutdownGracefully);
    }

    @Override
    protected String print0() {
        return "Proxy Stream Client [%s] %s:%s -> %s:%d"
                .formatted(this.proxyName, this.connectHost, this.connectPort, this.proxyHost, this.proxyPort);
    }
}