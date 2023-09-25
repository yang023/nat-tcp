package cn.nat.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.util.ByteProcessor;

/**
 * @author yang
 */
final class LineReader {

    private final String delimiter;

    LineReader(String delimiter) {
        this.delimiter = delimiter;
    }

    String read(ByteBuf buf) {
        Processor processor = new Processor(delimiter);
        int i = buf.forEachByte(processor);
        if (i == -1) {
            buf.readerIndex(buf.writerIndex());
        } else {
            int readerIndex = Math.min(i - 1 + delimiter.length(), buf.writerIndex());
            buf.readerIndex(readerIndex);
        }

        return processor.toString();
    }

    static class Processor implements ByteProcessor {
        private final StringBuilder appendable = new StringBuilder();

        private final char[] delimiter;
        private int delIdx = 0;

        Processor(String delimiter) {
            this.delimiter = delimiter.toCharArray();
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char) (value & 0xFF);
            if (nextByte == delimiter[delIdx]) {
                delIdx++;
                return delIdx != delimiter.length;
            } else {
                delIdx = 0;
                appendable.append(nextByte);
                return true;
            }
        }

        @Override
        public String toString() {
            return appendable.toString();
        }
    }
}