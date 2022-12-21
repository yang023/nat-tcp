package cn.slackoff.nat.core.protocol.codec;

import cn.slackoff.nat.core.protocol.MessageEncoder;
import cn.slackoff.nat.core.tools.Json;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author yang
 */
public class JsonMessageEncoder implements MessageEncoder {

    @Override
    public void encode(Object object, ByteBuf out) {
        String s = Json.toJson(object);
        out.writeCharSequence(s, StandardCharsets.UTF_8);
    }
}
