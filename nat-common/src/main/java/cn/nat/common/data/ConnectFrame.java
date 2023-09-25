package cn.nat.common.data;

import cn.nat.common.utils.BufferUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author yang
 */
public class ConnectFrame extends AbstractData {
    public static final String FRAME_COMMAND = "connect";

    /**
     * 隧道代理名称
     */
    private String tunnel;

    public String tunnel() {
        return tunnel;
    }

    public ConnectFrame tunnel(String tunnel) {
        this.tunnel = tunnel;
        return this;
    }

    @Override
    protected void readFrom(ByteBuf content) {
        this.tunnel = BufferUtil.readLine(content);
    }

    @Override
    protected void writeTo(ByteBuf content) {
        BufferUtil.writeLine(content, this.tunnel);
    }
}