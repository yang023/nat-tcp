package cn.slackoff.nat.core.boot;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.function.Consumer;

/**
 * @author yang
 */
public class CustomizeNettyClient extends AbstractNettyClient {

    private Consumer<NioSocketChannel> configurer;
    private Consumer<ChannelFuture> errorCallback = channelFuture -> channelFuture.channel().close();

    @Override
    protected void configClient(Bootstrap bootstrap) {
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                configurer.accept(ch);
            }
        });
    }

    @Override
    protected void onError(ChannelFuture channelFuture) {
        this.errorCallback.accept(channelFuture);
    }

    public void setConfigurer(Consumer<NioSocketChannel> configurer) {
        this.configurer = configurer;
    }

    public void addConfigurer(Consumer<NioSocketChannel> configurer) {
        if (this.configurer == null) {
            this.configurer = configurer;
        } else {
            this.configurer = this.configurer.andThen(configurer);
        }
    }

    public void setErrorCallback(Consumer<ChannelFuture> errorCallback) {
        this.errorCallback = errorCallback;
    }
}
