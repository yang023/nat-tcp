package cn.nat.app.client.command.proxy;

import cn.nat.app.client.data.ProxyTunnel;
import cn.nat.common.container.ConfigurableContainerSupport;
import cn.nat.common.container.Resource;
import cn.nat.common.netty.NettyInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author yang
 */
final class ProxyMessageSender extends ConfigurableContainerSupport<ProxyTunnel, ProxyMessageSender> {
    static final AttributeKey<Consumer<Object>> TARGET_MESSAGING_KEY = AttributeKey.valueOf("target-channel-messaging");

    private static final ConcurrentMap<Channel, ProxyMessageSender> senderMapping = new ConcurrentHashMap<>();
    private final ProxyFactory factory;
    private Channel channel;

    private ProxyMessageSender() {
        this.factory = ProxyFactory.instance;
    }

    public ChannelFuture send(Object msg) {
        return channel.writeAndFlush(msg);
    }

    public ChannelFuture closeFuture() {
        return channel.closeFuture();
    }

    @Override
    protected Collection<Resource> start(Context context, ProxyTunnel config) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = NettyInitializer.createBootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        Consumer<Object> consumer = channel.attr(TARGET_MESSAGING_KEY).get();
                        if (consumer != null) {
                            consumer.accept(msg);
                        }
                    }
                });
                NettyInitializer.withTrafficShaping(ch, factory.readRate, factory.writeRate);
            }
        });

        try {
            bootstrap.connect(config.host(), config.port()).addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {
                    channel = f.channel();
                } else {
                    stop();
                }
            }).sync();
        } catch (InterruptedException e) {
            stop();
            Thread.currentThread().interrupt();
        }

        return List.of(eventLoopGroup::shutdownGracefully);
    }

    @Override
    protected String print(ProxyTunnel config) {
        return null;
    }

    void registerMessageListener(Consumer<Object> listener) {
        channel.attr(TARGET_MESSAGING_KEY).set(listener);
    }

    static ProxyMessageSender getOrCreate(Channel channel) {
        return senderMapping.computeIfAbsent(channel, __ -> {
            ProxyMessageSender sender = new ProxyMessageSender();
            channel.closeFuture().addListener(f -> senderMapping.remove(channel, sender));
            return sender;
        });
    }

    static void closeSender(Channel channel) {
        ProxyMessageSender sender = senderMapping.get(channel);
        if (sender == null) {
            return;
        }

        sender.stop();
    }

}
