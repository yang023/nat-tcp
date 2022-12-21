package cn.slackoff.nat.core.protocol;

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * @author yang
 */
public interface FrameInput {
    Command command();

    ContentType contentType();

    Headers headers();

    Channel channel();

    ByteBuf input();

    String string();

    <T> T json(Class<T> type);

    <T> T json(TypeReference<T> type);
}
