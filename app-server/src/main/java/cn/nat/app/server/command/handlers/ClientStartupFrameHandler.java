package cn.nat.app.server.command.handlers;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.common.LoggerFactory;
import cn.nat.common.data.ClientStartupFrame;
import cn.nat.common.data.ServerEndpointFrame;
import cn.nat.common.protocol.AbstractFrameHandler;
import cn.nat.common.protocol.Frame;
import cn.nat.common.utils.DataSize;
import io.netty.channel.Channel;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author yang
 */
public class ClientStartupFrameHandler extends AbstractFrameHandler {
    private final Logger logger = LoggerFactory.getLogger("server.client-startup-handler");

    private final ServerConfig config;
    private final BiConsumer<String, Channel> addChannelHandler;

    public ClientStartupFrameHandler(ServerConfig config, BiConsumer<String, Channel> addChannelHandler) {
        super(ClientStartupFrame.FRAME_COMMAND);

        this.config = config;
        this.addChannelHandler = addChannelHandler;
    }

    @Override
    public void handle(Context ctx, Frame input) throws Exception {
        ClientStartupFrame frame = new ClientStartupFrame();
        frame.readFrame(input);

        // 验证连接申请信息，返回服务端口

        List<String> tunnels = frame.tunnels();
        for (String tunnel : tunnels) {
            addChannelHandler.accept(tunnel, ctx.channel());
        }
        logger.info("注册client: {}", frame.clientId());

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
