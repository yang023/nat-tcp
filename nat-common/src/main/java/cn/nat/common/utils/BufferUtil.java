package cn.nat.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author yang
 */
public final class BufferUtil {
    public static final String DEFAULT_LINE_SEPARATOR = "\r\n";

    public static void print(ByteBuf buf) {
        StringBuilder builder = new StringBuilder();
        builder.append("Reader index: %d\t".formatted(buf.readerIndex()));
        builder.append("Writer index: %d\t".formatted(buf.writerIndex()));
        builder.append("\r\n");
        ByteBufUtil.appendPrettyHexDump(builder, buf);
        System.out.println(builder);
    }

    public static String readLine(ByteBuf buf, String lineSep) {
        String separator = getSeparator(lineSep);
        LineReader reader = new LineReader(separator);
        return reader.read(buf);
    }

    public static String readLine(ByteBuf buf) {
        return readLine(buf, null);
    }

    public static void writeLine(ByteBuf buf, String content, String lineSep) {
        String separator = getSeparator(lineSep);
        buf.writeCharSequence(content, StandardCharsets.UTF_8);
        buf.writeCharSequence(separator, StandardCharsets.UTF_8);
    }

    public static void writeLine(ByteBuf buf, String content) {
        writeLine(buf, content, null);
    }

    public static ByteBuf writeLine(String content, String lineSep) {
        ByteBuf buf = Unpooled.buffer();
        writeLine(buf, content, lineSep);
        return buf;
    }

    public static ByteBuf writeLine(String content) {
        ByteBuf buf = Unpooled.buffer();
        writeLine(buf, content, null);
        return buf;
    }

    private BufferUtil() {
    }

    private static String getSeparator(String lineSep) {
        return lineSep == null || lineSep.isEmpty() || lineSep.isBlank() ? DEFAULT_LINE_SEPARATOR : lineSep;
    }
}