package cn.slackoff.nat.core.protocol.codec;

import cn.slackoff.nat.core.protocol.MessageEncoder;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author yang
 */
public class StringMessageEncoder implements MessageEncoder {
    @Override
    public void encode(Object object, ByteBuf out) {
        if (!(object instanceof String str)) {
            throw new IllegalArgumentException("Just string value are supported.");
        }

        out.writeCharSequence(str, StandardCharsets.UTF_8);
    }
}
