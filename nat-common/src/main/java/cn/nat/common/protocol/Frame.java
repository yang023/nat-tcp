package cn.nat.common.protocol;

import cn.nat.common.utils.BufferUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author yang
 */
public class Frame {
    public static final String FRAME_PROTOCOL;
    public static final int FRAME_PROTOCOL_SINGLE_LENGTH = 64;
    public static final String BASE_HEADER_ITEM_FORMATTER = "%64s";
    public static final String CRLF = "\r\n";

    private static final ByteBuf FRAME_PROTOCOL_BUF;

    static {
        // Gundam Seed Destiny...
        FRAME_PROTOCOL = BASE_HEADER_ITEM_FORMATTER.formatted("@_Infinite_Justice_@").replace(' ', '0');
        byte[] bytes = FRAME_PROTOCOL.getBytes(StandardCharsets.UTF_8);
        FRAME_PROTOCOL_BUF = Unpooled.wrappedUnmodifiableBuffer(Unpooled.wrappedBuffer(bytes));
    }

    private final String id;
    private final String command;
    private final ByteBuf content;

    public static Frame deserialize(ByteBuf buf) {
        if (buf.readableBytes() < FRAME_PROTOCOL_SINGLE_LENGTH) {
            return null;
        }
        buf.markReaderIndex();
        CharSequence sign = buf.readCharSequence(FRAME_PROTOCOL_SINGLE_LENGTH, StandardCharsets.UTF_8);
        if (!FRAME_PROTOCOL.contentEquals(sign)) {
            buf.resetReaderIndex();
            return null;
        }

        String id = BufferUtil.readLine(buf, CRLF);
        String command = BufferUtil.readLine(buf, CRLF);
        Frame frame = new Frame(id, command);
        ByteBuf frameContent = frame.content();

        frameContent.writeBytes(buf);

        return frame;
    }

    public Frame(String command) {
        this(UUID.randomUUID().toString().replace("-", ""), command);
    }

    protected Frame(String id, String command) {
        this.id = id;
        this.command = command;
        this.content = Unpooled.buffer();
    }

    public String id() {
        return id;
    }

    public String command() {
        return command;
    }

    public ByteBuf content() {
        return content;
    }

    public ByteBuf serialize() {
        CompositeByteBuf buffer = Unpooled.compositeBuffer();
        byte[] bytes = FRAME_PROTOCOL.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.wrappedUnmodifiableBuffer(Unpooled.wrappedBuffer(bytes));
        buffer.addComponent(true, buf);
        buffer.addComponent(true, BufferUtil.writeLine(this.id, CRLF));
        buffer.addComponent(true, BufferUtil.writeLine(this.command, CRLF));
        buffer.addComponent(true, this.content);
        return buffer;
    }

    Frame copy() {
        Frame frame = new Frame(this.id, this.command);
        frame.content().writeBytes(this.content());
        return frame;
    }
}