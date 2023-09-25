package cn.nat.common.data;

import cn.nat.common.utils.BufferUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author yang
 */
public class ServerEndpointFrame extends AbstractData {
    public static final String FRAME_COMMAND = "server_info";

    /**
     * 服务器代理通道的主机端口
     */
    private int port;

    private long readRate;
    private long writeRate;

    public int port() {
        return port;
    }

    public ServerEndpointFrame port(int port) {
        this.port = port;
        return this;
    }

    public long readRate() {
        return readRate;
    }

    public ServerEndpointFrame readRate(long readRate) {
        this.readRate = readRate;
        return this;
    }

    public long writeRate() {
        return writeRate;
    }

    public ServerEndpointFrame writeRate(long writeRate) {
        this.writeRate = writeRate;
        return this;
    }

    @Override
    protected void readFrom(ByteBuf content) {
        this.port = Integer.parseInt(BufferUtil.readLine(content));
        this.readRate = Long.parseLong(BufferUtil.readLine(content));
        this.writeRate = Long.parseLong(BufferUtil.readLine(content));
    }

    @Override
    protected void writeTo(ByteBuf content) {
        BufferUtil.writeLine(content, this.port + "");
        BufferUtil.writeLine(content, this.readRate + "");
        BufferUtil.writeLine(content, this.writeRate + "");
    }
}