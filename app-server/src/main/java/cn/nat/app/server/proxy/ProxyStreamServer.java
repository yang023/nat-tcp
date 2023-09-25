package cn.nat.app.server.proxy;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.app.server.utils.AbstractNettyServerContainer;
import cn.nat.common.container.Resource;
import cn.nat.common.protocol.FrameHandlerRegistry;
import cn.nat.common.protocol.StreamFrameDispatcher;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 从{@link AbstractGatewayServer}实现类中接收请求报文，转发到客户端
 *
 * @author yang
 */
@Component
public class ProxyStreamServer extends AbstractNettyServerContainer<ProxyStreamServer> {
    private final Map<String, ChannelGroup> channelGroups = new ConcurrentHashMap<>();

    private final FrameHandlerRegistry registry = new FrameHandlerRegistry();

    @Autowired
    @Override
    public ProxyStreamServer config(ServerConfig config) {
        return super.config(config);
    }

    @Override
    protected Collection<Resource> start(Context context, ServerConfig config) {
        initializeHandlers();
        return super.start(context, config);
    }

    @Override
    protected int resolvePort(ServerConfig config) {
        return config.getStreamPort();
    }

    @Override
    protected void configure(Context context, NioSocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new StreamFrameDispatcher(registry));
    }

    @Override
    public String print(ServerConfig config) {
        return "Proxy Stream Server %d".formatted(config.getStreamPort());
    }

    public ChannelGroup findChannelGroup(String name) {
        return channelGroups.computeIfAbsent(name, __ -> new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    }

    private void initializeHandlers() {
        registry.register(() -> new StreamConnectFrameHandler(this::findChannelGroup));
    }
}