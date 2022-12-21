package cn.slackoff.nat.core.boot;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;

import java.util.function.Consumer;

/**
 * @author yang
 */
public class CustomizeNettyServer extends AbstractNettyServer {

    private Consumer<ServerBootstrap> configurer;

    private Consumer<ChannelFuture> errorCallback = channelFuture -> channelFuture.channel().close();

    @Override
    protected void onError(ChannelFuture channelFuture) {
        this.errorCallback.accept(channelFuture);
    }

    @Override
    protected void configServer(ServerBootstrap bootstrap) {
        this.configurer.accept(bootstrap);
    }

    public void setConfigurer(Consumer<ServerBootstrap> configurer) {
        this.configurer = configurer;
    }

    public void setErrorCallback(Consumer<ChannelFuture> errorCallback) {
        this.errorCallback = errorCallback;
    }
}
