package cn.nat.app.server.proxy;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.app.server.utils.AbstractNettyServerContainer;
import cn.nat.common.netty.NettyInitializer;
import cn.nat.common.utils.DataSize;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.checkerframework.checker.units.qual.C;

/**
 * @author yang
 */
public abstract class AbstractGatewayServer
        extends AbstractNettyServerContainer<ServerConfig.Gateway, AbstractGatewayServer> {

    @Override
    protected final void configure(Context context, ServerConfig.Gateway config, NioSocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        applyGatewayCodec(pipeline);
        NettyInitializer.withTrafficShaping(channel, DataSize.parseToBytes("200MB"), DataSize.parseToBytes("200MB"));

        pipeline.addLast(new ProxyGatewayHandler(createMessageHandler()));
    }

    protected void applyConfig(C config) {}

    protected abstract void applyGatewayCodec(ChannelPipeline pipeline);
    protected abstract ProxyMessageHandler createMessageHandler();
}
