package cn.slackoff.nat.core.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link java.nio.ByteBuffer} 内容调试工具
 *
 * @author yang
 */
public final class ByteLogger {
    private static final Logger log = LoggerFactory.getLogger(ByteLogger.class);

    private ByteLogger() {
    }

    public static void info(byte[] bytes) {
        info(Unpooled.wrappedBuffer(bytes));
    }

    public static void info(ByteBuf buffer) {
        if (!log.isInfoEnabled()) {
            return;
        }
        String buf = toBuffString(buffer);
        log.info(buf);
    }

    public static void debug(byte[] bytes) {
        debug(Unpooled.wrappedBuffer(bytes));
    }

    public static void debug(ByteBuf buffer) {
        if (!log.isDebugEnabled()) {
            return;
        }
        String buf = toBuffString(buffer);
        log.debug(buf);
    }

    public static String toBuffString(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(StringUtil.NEWLINE);
        ByteBufUtil.appendPrettyHexDump(buf, buffer);
        return buf.toString();
    }
}
