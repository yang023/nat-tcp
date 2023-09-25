package cn.nat.common.data;

import cn.nat.common.utils.BufferUtil;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yang
 */
public class ClientStartupFrame extends AbstractData {
    public static final String FRAME_COMMAND = "client-startup";

    private String clientId;
    private List<String> tunnels;

    public String clientId() {
        return clientId;
    }

    public ClientStartupFrame clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public List<String> tunnels() {
        return tunnels;
    }

    public ClientStartupFrame tunnels(List<String> tunnels) {
        this.tunnels = tunnels;
        return this;
    }

    @Override
    protected void readFrom(ByteBuf content) {
        this.clientId = BufferUtil.readLine(content);

        List<String> list = new ArrayList<>();
        while (content.isReadable()) {
            list.add(BufferUtil.readLine(content));
        }
        this.tunnels = new ArrayList<>(list);
    }

    @Override
    protected void writeTo(ByteBuf content) {
        BufferUtil.writeLine(content, this.clientId);

        for (String tunnel : this.tunnels) {
            BufferUtil.writeLine(content, tunnel);
        }
    }
}
