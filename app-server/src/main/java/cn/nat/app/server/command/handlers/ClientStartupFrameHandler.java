package cn.nat.app.server.command.handlers;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.common.data.ClientStartupFrame;
import cn.nat.common.data.ServerEndpointFrame;
import cn.nat.common.protocol.AbstractFrameHandler;
import cn.nat.common.protocol.Frame;
import cn.nat.common.utils.DataSize;

/**
 * @author yang
 */
public class ClientStartupFrameHandler extends AbstractFrameHandler {

    private final ServerConfig config;

    public ClientStartupFrameHandler(ServerConfig config) {
        super(ClientStartupFrame.FRAME_COMMAND);

        this.config = config;
    }

    @Override
    public void handle(Context ctx, Frame input) throws Exception {
        ClientStartupFrame frame = new ClientStartupFrame();
        frame.readFrame(input);

        // 验证连接申请信息，返回服务端口

        ServerEndpointFrame serverEndpoint = new ServerEndpointFrame();

        // @formatter:off
        Frame output = serverEndpoint
                .port(config.getStreamPort())
                // TODO console configure...
                .readRate(DataSize.parseToBytes("100MB"))
                .writeRate(DataSize.parseToBytes("50MB"))
                .createFrame();
        // @formatter:on

        ctx.sendResponse(output);
    }
}
