package cn.nat.app.client.command.handlers;

import cn.nat.app.client.command.proxy.ProxyFactory;
import cn.nat.common.data.ServerEndpointFrame;
import cn.nat.common.protocol.AbstractFrameHandler;
import cn.nat.common.protocol.Frame;

/**
 * @author yang
 */
public final class ServerEndpointFrameHandler extends AbstractFrameHandler {

    private final String streamHost;

    public ServerEndpointFrameHandler(String streamHost) {
        super(ServerEndpointFrame.FRAME_COMMAND);
        this.streamHost = streamHost;
    }

    @Override
    public void handle(Context ctx, Frame input) throws Exception {
        ServerEndpointFrame serverEndpoint = new ServerEndpointFrame();
        serverEndpoint.readFrame(input);

        // 创建连接器
        int port = serverEndpoint.port();
        long readRate = serverEndpoint.readRate();
        long writeRate = serverEndpoint.writeRate();

        new ProxyFactory.Builder().streamHost(streamHost)
                                  .streamPort(port)
                                  .readRate(readRate)
                                  .writeRate(writeRate)
                                  .build();
    }
}