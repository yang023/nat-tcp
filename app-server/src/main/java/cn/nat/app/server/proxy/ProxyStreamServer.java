package cn.nat.app.server.proxy;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.app.server.proxy.handlers.ConnectFrameHandler;
import cn.nat.app.server.utils.AbstractNettyServerContainer;
import cn.nat.common.protocol.FrameHandlerRegistry;
import cn.nat.common.protocol.StreamFrameDispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yang
 */
@Component
public class ProxyStreamServer extends AbstractNettyServerContainer<ServerConfig, ProxyStreamServer> {
    private final FrameHandlerRegistry registry = new FrameHandlerRegistry();

    @Autowired
    @Override
    public ProxyStreamServer config(ServerConfig config) {
        return super.config(config);
    }

    @Override
    protected int resolvePort(ServerConfig config) {
        return config.getStreamPort();
    }

    @Override
    protected void configure(Context context, ServerConfig config, NioSocketChannel channel) {
        initializeHandlers();

        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new StreamFrameDispatcher(registry));
        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                StreamChannelMapping.triggerMessage(ctx.channel(), msg);
            }
        });
    }

    @Override
    protected String print(ServerConfig config) {
        return "Proxy Stream Server %d".formatted(config.getStreamPort());
    }

    private void initializeHandlers() {
        registry.register(new ConnectFrameHandler());
    }
}
