package cn.slackoff.nat.core.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * @author yang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OriginalBytesHolder {
    private static final MultiValueMap<String, ByteBuf> HOLDER_MAP = new LinkedMultiValueMap<>();

    public static void saveChannelBytes(Channel channel, ByteBuf buf) {
        String channelId = channelId(channel);
        synchronized (HOLDER_MAP) {
            HOLDER_MAP.add(channelId, buf);
        }

        channel.closeFuture().addListener(e -> reset(channel));
    }

    public static ByteBuf findChannelBytes(Channel channel) {
        String channelId = channelId(channel);
        synchronized (HOLDER_MAP) {
            List<ByteBuf> byteBuf = HOLDER_MAP.get(channelId);
            ByteBuf buffer = Unpooled.directBuffer();

            for (ByteBuf buf : byteBuf) {
                buffer.writeBytes(buf);
            }

            return buffer;
        }
    }

    public static void reset(Channel channel) {
        String channelId = channelId(channel);
        synchronized (HOLDER_MAP) {
            HOLDER_MAP.remove(channelId);
        }
    }


    private static String channelId(Channel channel) {
        return channel.id().asLongText();
    }
}
