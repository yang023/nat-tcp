package cn.nat.common.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

/**
 * @author yang
 */
public final class NettyInitializer {
    private static final String TRAFFIC_SHAPING_HANDLER_KEY = "TrafficShaping";
    private static final RecvByteBufAllocator DEFAULT_RECV_ALLOCATOR = new FixedRecvByteBufAllocator(4096);

    public static Bootstrap createBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, DEFAULT_RECV_ALLOCATOR);
        return bootstrap;
    }

    public static ServerBootstrap createServerBootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, DEFAULT_RECV_ALLOCATOR);
        bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, DEFAULT_RECV_ALLOCATOR);
        return bootstrap;
    }

    public static void withTrafficShaping(Channel channel, long readLimit, long writeLimit) {
        ChannelPipeline pipeline = channel.pipeline();
        ChannelHandler handler = pipeline.get(TRAFFIC_SHAPING_HANDLER_KEY);
        ChannelTrafficShapingHandler newHandler = new ChannelTrafficShapingHandler(writeLimit, readLimit);

        if (handler == null) {
            pipeline.addFirst(TRAFFIC_SHAPING_HANDLER_KEY, newHandler);
        } else {
            pipeline.replace(handler, TRAFFIC_SHAPING_HANDLER_KEY, newHandler);
        }
    }
}