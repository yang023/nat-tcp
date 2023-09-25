package cn.nat.app.client.proxy;

import cn.nat.common.container.ContainerSupport;
import cn.nat.common.container.Resource;
import cn.nat.common.netty.NettyInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yang
 */
public final class InternalProxyClient extends ContainerSupport<InternalProxyClient> {
    private static final AttributeKey<InternalProxyClient> PROXY_CLIENT_KEY = AttributeKey.valueOf("proxy-client");

    private final Channel stream;

    private String host;
    private int port;

    private long readRate;
    private long writeRate;

    private volatile Channel proxied;

    public static InternalProxyClient create(Channel stream) {
        Attribute<InternalProxyClient> attr = stream.attr(PROXY_CLIENT_KEY);
        InternalProxyClient client = new InternalProxyClient(stream);
        attr.setIfAbsent(client);
        return client;
    }

    public InternalProxyClient proxy(String host, int port) {
        this.host = host;
        this.port = port;
        return this;
    }

    public InternalProxyClient limit(long readRate, long writeRate) {
        this.readRate = readRate;
        this.writeRate = writeRate;
        return this;
    }

    public static InternalProxyClient resolve(Channel stream) {
        Attribute<InternalProxyClient> attr = stream.attr(PROXY_CLIENT_KEY);
        return attr.get();
    }

    private InternalProxyClient(Channel stream) {
        this.stream = stream;
    }

    public synchronized void send(Object object) {
        this.proxied.writeAndFlush(object).addListener(f -> {
            if (f.isSuccess()) {
                this.proxied.read();
            }
        });
    }

    @Override
    protected Collection<Resource> start0(Context context) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = NettyInitializer.createBootstrap();
        bootstrap.channel(NioSocketChannel.class).group(eventLoopGroup);

        ChannelFuture future = bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                NettyInitializer.withTrafficShaping(ch, writeRate, readRate);
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        stream.writeAndFlush(msg).addListener(f -> {
                            if (f.isSuccess()) {
                                stream.read();
                            }
                        });
                    }
                });
            }
        }).connect(host, port);

        proxied = future.channel();

        return Collections.singletonList(eventLoopGroup::shutdownGracefully);
    }

    @Override
    protected String print0() {
        return "Internal Proxy Client %s:%s".formatted(this.host, this.port);
    }

}