package cn.slackoff.nat.core.protocol.codec;

import cn.slackoff.nat.core.protocol.MessageEncoder;
import io.netty.buffer.ByteBuf;

/**
 * @author yang
 */
public class StreamMessageEncoder implements MessageEncoder {
    @Override
    public void encode(Object object, ByteBuf out) {
        if (object instanceof byte[] byteArr) {
            out.writeBytes(byteArr);
            return;
        }

        if (object instanceof ByteBuf buf) {
            out.writeBytes(buf);
            return;
        }
        
        throw new IllegalArgumentException("Just ByteBuf or byte[] value are supported.");
    }
}
