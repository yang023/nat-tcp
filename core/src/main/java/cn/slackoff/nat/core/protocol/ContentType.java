package cn.slackoff.nat.core.protocol;

import cn.slackoff.nat.core.protocol.codec.JsonMessageEncoder;
import cn.slackoff.nat.core.protocol.codec.StreamMessageEncoder;
import cn.slackoff.nat.core.protocol.codec.StringMessageEncoder;
import io.netty.buffer.ByteBufAllocator;

import java.util.List;

/**
 * @author yang
 */
public abstract class ContentType {
    public static final ContentType NONE = new ContentType((byte) 0x0, new StreamMessageEncoder()) {
        @Override
        public Frame createMessage(Command command) {
            return new Frame(command, this, ByteBufAllocator.DEFAULT.buffer());
        }
    };
    public static final ContentType STREAM = new ContentType((byte) 0x01, new StreamMessageEncoder()) {
        @Override
        public Frame createMessage(Command command) {
            return new Frame(command, this, ByteBufAllocator.DEFAULT.directBuffer());
        }
    };
    public static final ContentType STRING = new ContentType((byte) 0x02, new StringMessageEncoder()) {
        @Override
        public Frame createMessage(Command command) {
            return new Frame(command, this, ByteBufAllocator.DEFAULT.buffer());
        }
    };
    public static final ContentType JSON = new ContentType((byte) 0x03, new JsonMessageEncoder()) {
        @Override
        public Frame createMessage(Command command) {
            return new Frame(command, this, ByteBufAllocator.DEFAULT.buffer());
        }
    };

    public static final List<ContentType> AVAILABLE_CONTENT_TYPES = List.of(STREAM, STRING, JSON);

    private final byte value;

    private final MessageEncoder encoder;

    private ContentType(byte value, MessageEncoder encoder) {
        this.value = value;
        this.encoder = encoder;
    }

    public byte getValue() {
        return value;
    }

    public abstract Frame createMessage(Command command);

    public MessageEncoder getEncoder() {
        return encoder;
    }
}
