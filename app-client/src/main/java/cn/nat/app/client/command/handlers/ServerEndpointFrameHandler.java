package cn.nat.app.client.command.handlers;

import cn.nat.app.client.config.ClientConfig;
import cn.nat.app.client.data.ProxyTunnel;
import cn.nat.app.client.proxy.ProxyStreamClient;
import cn.nat.common.container.Container;
import cn.nat.common.data.ServerEndpointFrame;
import cn.nat.common.protocol.AbstractFrameHandler;
import cn.nat.common.protocol.Frame;

import java.util.List;

/**
 * @author yang
 */
public final class ServerEndpointFrameHandler extends AbstractFrameHandler {
    private final Container.Context context;
    private final ClientConfig config;
    private final List<ProxyTunnel> tunnels;

    public ServerEndpointFrameHandler(Container.Context context, ClientConfig config, List<ProxyTunnel> tunnels) {
        super(ServerEndpointFrame.FRAME_COMMAND);
        this.context = context;
        this.config = config;
        this.tunnels = tunnels;
    }

    @Override
    public void handle(Context ctx, Frame input) throws Exception {
        ServerEndpointFrame serverEndpoint = new ServerEndpointFrame();
        serverEndpoint.readFrame(input);

        // 创建连接器
        int port = serverEndpoint.port();

        for (ProxyTunnel tunnel : tunnels) {
            for (int i = 0; i < tunnel.poolSize(); i++) {
                ProxyStreamClient container = new ProxyStreamClient(tunnel.name())
                        .connect(config.getServer().getHost(), port)
                        .proxyTo(tunnel.host(), tunnel.port());
                context.addAndStart(container);
            }
        }
    }
}