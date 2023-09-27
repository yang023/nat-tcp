package cn.nat.common.data;

import cn.nat.common.utils.BufferUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author yang
 */
public class ChannelRequestFrame extends AbstractData {
    public static final String FRAME_COMMAND = "channel-request";

    private String requestId;

    private String tunnel;

    public String requestId() {
        return requestId;
    }

    public ChannelRequestFrame requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String tunnel() {
        return tunnel;
    }

    public ChannelRequestFrame tunnel(String tunnel) {
        this.tunnel = tunnel;
        return this;
    }

    @Override
    protected void readFrom(ByteBuf content) {
        this.requestId = BufferUtil.readLine(content);
        this.tunnel = BufferUtil.readLine(content);
    }

    @Override
    protected void writeTo(ByteBuf content) {
        BufferUtil.writeLine(content, requestId);
        BufferUtil.writeLine(content, tunnel);
    }
}
