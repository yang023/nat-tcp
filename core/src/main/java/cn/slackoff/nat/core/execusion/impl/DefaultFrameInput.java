package cn.slackoff.nat.core.execusion.impl;

import cn.slackoff.nat.core.protocol.*;
import cn.slackoff.nat.core.tools.Json;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * @author yang
 */
@RequiredArgsConstructor
public class DefaultFrameInput implements FrameInput {

    private final Channel channel;
    private final Frame frame;

    private boolean read;

    @Override
    public Command command() {
        return this.frame.command();
    }

    @Override
    public ContentType contentType() {
        return this.frame.contentType();
    }

    @Override
    public Headers headers() {
        return this.frame.headers();
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public ByteBuf input() {
        if (this.read) {
            throw new IllegalStateException("Request body has been already read.");
        }
        this.read = true;
        return this.frame.body();
    }

    @Override
    public String string() {
        ByteBuf buf = input();
        return (String) buf.readCharSequence(buf.readableBytes(), StandardCharsets.UTF_8);
    }

    @Override
    public <T> T json(Class<T> type) {
        ByteBuf buf = input();
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        return Json.fromJson(arr, type);
    }

    @Override
    public <T> T json(TypeReference<T> type) {
        ByteBuf buf = input();
        byte[] arr = new byte[buf.readableBytes()];
        buf.readBytes(arr);
        return Json.fromJson(arr, type);
    }
}
