package cn.nat.app.client.command.handlers;

import cn.nat.app.client.command.proxy.ProxyFactory;
import cn.nat.app.client.data.ProxyTunnel;
import cn.nat.common.data.ChannelRequestFrame;
import cn.nat.common.protocol.AbstractFrameHandler;
import cn.nat.common.protocol.Frame;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author yang
 */
public class ChannelRequestFrameHandler extends AbstractFrameHandler {
    private final List<ProxyTunnel> tunnels;

    public ChannelRequestFrameHandler(List<ProxyTunnel> tunnels) {
        super(ChannelRequestFrame.FRAME_COMMAND);
        this.tunnels = Collections.unmodifiableList(tunnels);
    }

    @Override
    public void handle(Context ctx, Frame input) throws Exception {
        ChannelRequestFrame channelRequest = new ChannelRequestFrame();
        channelRequest.readFrame(input);

        for (ProxyTunnel tunnel : tunnels) {
            if (Objects.equals(tunnel.name(), channelRequest.tunnel())) {
                ProxyFactory.createProxy(tunnel, channelRequest.requestId());
                return;
            }
        }
    }
}
