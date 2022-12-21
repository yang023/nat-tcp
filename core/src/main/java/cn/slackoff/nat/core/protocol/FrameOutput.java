package cn.slackoff.nat.core.protocol;

import io.netty.channel.ChannelFuture;

import java.util.function.Consumer;

/**
 * @author yang
 */
public interface FrameOutput {
    FrameOutput command(Command command);

    FrameOutput contentType(ContentType contentType);

    FrameOutput headers(Consumer<Headers> headersConsumer);

    ChannelFuture write(Object body);

    ChannelFuture write();
}
