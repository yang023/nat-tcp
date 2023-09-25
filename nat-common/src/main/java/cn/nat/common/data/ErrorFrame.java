package cn.nat.common.data;

import cn.nat.common.utils.BufferUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author yang
 */
public final class ErrorFrame extends AbstractData {
    public static final String FRAME_COMMAND = "error";

    private int code = 500;
    private String message;

    public int code() {
        return code;
    }

    public ErrorFrame code(int code) {
        this.code = code;
        return this;
    }

    public String message() {
        return message;
    }

    public ErrorFrame message(String message) {
        this.message = message;
        return this;
    }

    @Override
    protected void readFrom(ByteBuf content) {
        this.code = content.readInt();
        this.message = BufferUtil.readLine(content);
    }

    @Override
    protected void writeTo(ByteBuf content) {
        content.writeInt(this.code);
        BufferUtil.writeLine(content, this.message);
    }
}