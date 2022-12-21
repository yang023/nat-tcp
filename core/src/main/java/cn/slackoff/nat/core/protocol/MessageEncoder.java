package cn.slackoff.nat.core.protocol;

import io.netty.buffer.ByteBuf;

/**
 * @author yang
 */
public interface MessageEncoder {

    void encode(Object object, ByteBuf out);
}
