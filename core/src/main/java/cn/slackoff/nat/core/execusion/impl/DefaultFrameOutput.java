package cn.slackoff.nat.core.execusion.impl;

import cn.slackoff.nat.core.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * @author yang
 */
@RequiredArgsConstructor
public class DefaultFrameOutput implements FrameOutput {
    private final Channel channel;

    private Command command;
    private ContentType contentType;

    private Consumer<Headers> headersConsumer;

    @Override
    public FrameOutput command(Command command) {
        this.command = command;
        return this;
    }

    @Override
    public FrameOutput contentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public FrameOutput headers(Consumer<Headers> headersConsumer) {
        this.headersConsumer = headersConsumer;
        return this;
    }

    @Override
    public ChannelFuture write(Object body) {
        Frame message = contentType.createMessage(this.command);

        if (this.headersConsumer != null) {
            Headers headers = message.headers();
            this.headersConsumer.accept(headers);
        }

        MessageEncoder encoder = contentType.getEncoder();

        if (body != null) {
            ByteBuf buf = message.body();
            encoder.encode(body, buf);
        }

        return this.channel.writeAndFlush(message);
    }

    @Override
    public ChannelFuture write() {
        return write(null);
    }
}
