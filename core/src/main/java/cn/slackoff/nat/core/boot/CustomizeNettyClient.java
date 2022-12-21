package cn.slackoff.nat.core.boot;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

import java.util.function.Consumer;

/**
 * @author yang
 */
public class CustomizeNettyClient extends AbstractNettyClient {

    private Consumer<Bootstrap> configurer;
    private Consumer<ChannelFuture> errorCallback = channelFuture -> channelFuture.channel().close();

    @Override
    protected void configClient(Bootstrap bootstrap) {
        this.configurer.accept(bootstrap);
    }

    @Override
    protected void onError(ChannelFuture channelFuture) {
        this.errorCallback.accept(channelFuture);
    }

    public void setConfigurer(Consumer<Bootstrap> configurer) {
        this.configurer = configurer;
    }

    public void setErrorCallback(Consumer<ChannelFuture> errorCallback) {
        this.errorCallback = errorCallback;
    }
}
