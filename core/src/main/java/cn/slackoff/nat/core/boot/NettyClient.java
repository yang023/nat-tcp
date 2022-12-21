package cn.slackoff.nat.core.boot;

import io.netty.channel.ChannelFutureListener;

/**
 * @author yang
 */
public interface NettyClient {

    void connect(String host, int port);

    void connect(String endpoint);

    void onClosed(ChannelFutureListener channelFutureConsumer);
}
