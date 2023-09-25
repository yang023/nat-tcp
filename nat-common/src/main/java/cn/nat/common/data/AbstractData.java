package cn.nat.common.data;

import cn.nat.common.protocol.Frame;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;

/**
 * @author yang
 */
abstract class AbstractData {
    private static final String FRAME_COMMAND_STATIC_FIELD = "FRAME_COMMAND";

    public final void readFrame(Frame frame) {
        ByteBuf content = frame.content();
        readFrom(content);
    }

    public final Frame createFrame() {
        String command;
        try {
            Field field = this.getClass().getField(FRAME_COMMAND_STATIC_FIELD);
            field.setAccessible(true);
            command = (String)field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }

        Frame frame = new Frame(command);
        writeTo(frame.content());
        return frame;
    }

    protected abstract void readFrom(ByteBuf content);
    protected abstract void writeTo(ByteBuf content);
}