package cn.slackoff.nat.core.protocol;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author yang
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Frame {
    private final Command command;
    private final ContentType contentType;
    private final ByteBuf body;

    private final Headers headers = new Headers();

    public Command command() {
        return command;
    }

    public ByteBuf body() {
        return body;
    }

    public ContentType contentType() {
        return contentType;
    }

    public Headers headers() {
        return headers;
    }
}
