package cn.slackoff.nat.core.execusion;

import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.ContentType;
import cn.slackoff.nat.core.protocol.Frame;
import cn.slackoff.nat.core.protocol.Headers;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.internal.SystemPropertyUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author yang
 */
public final class FrameCodec extends ByteToMessageCodec<Frame> {
    private static final int DEFAULT_MAGIC = 9527;
    private static final int MAGIC;

    private static final byte LINER = 0x0;

    private static final Splitter HEADER_SPLITTER = Splitter.on("##").omitEmptyStrings();
    private static final Joiner HEADER_JOINER = Joiner.on("##").skipNulls();

    static {
        String s = SystemPropertyUtil.get("nat.common.magic");
        if (s == null || s.isBlank() || s.isEmpty()) {
            MAGIC = DEFAULT_MAGIC;
        } else {
            int i;
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                i = DEFAULT_MAGIC;
            }
            MAGIC = i;
        }
    }

    private void string2Headers(Headers headers, CharSequence string) {
        HEADER_SPLITTER.splitToList(string).stream().map(HeaderItem::new)
                       .forEach(it -> headers.set(it.key(), it.value()));
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Frame msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC);
        out.writeByte(msg.command().getCode());
        out.writeByte(msg.contentType().getValue());
        out.writeByte(LINER);

        CharSequence s = headers2String(msg.headers());
        out.writeInt(s.length());
        out.writeCharSequence(s, StandardCharsets.UTF_8);
        out.writeByte(LINER);

        out.writeBytes(msg.body());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int anInt = in.getInt(0);
        if (anInt != MAGIC) {
            return;
        }

        in.readInt();

        byte b = in.readByte();

        Optional<Command> commandOptional = Command.ofCode(b);
        if (commandOptional.isEmpty()) {
            in.resetReaderIndex();
            return;
        }
        Command command = commandOptional.get();

        byte b1 = in.readByte();
        ContentType contentType = ContentType.AVAILABLE_CONTENT_TYPES
                .stream()
                .filter(it -> it.getValue() == b1)
                .findFirst()
                .orElse(null);
        if (contentType == null) {
            in.resetReaderIndex();
            return;
        }

        // LINER
        in.readByte();

        Frame message = contentType.createMessage(command);

        int length = in.readInt();
        CharSequence string = in.readCharSequence(length, StandardCharsets.UTF_8);
        string2Headers(message.headers(), string);

        // LINER
        in.readByte();

        ByteBuf body = message.body();
        body.writeBytes(in);

        out.add(message);
    }

    private CharSequence headers2String(Headers headers) {
        List<String> list = headers.stream()
                                   .map(HeaderItem::new).map(HeaderItem::toString)
                                   .toList();
        return HEADER_JOINER.join(list);
    }

    static class HeaderItem {
        private static final Joiner JOINER = Joiner.on("=").skipNulls();
        private static final Splitter SPLITTER = Splitter.on("=").omitEmptyStrings();
        private final Map.Entry<String, String> entry;

        HeaderItem(String str) {
            List<String> strings = SPLITTER.splitToList(str);
            if (strings.size() != 2) {
                entry = null;
            } else {
                entry = Map.entry(strings.get(0), strings.get(1));
            }
        }

        HeaderItem(Map.Entry<String, String> entry) {
            this.entry = entry;
        }

        @Override
        public String toString() {
            if (entry == null) {
                return "";
            }
            return JOINER.join(entry.getKey(), entry.getValue());
        }

        String key() {
            return entry.getKey();
        }

        String value() {
            return entry.getValue();
        }
    }
}
