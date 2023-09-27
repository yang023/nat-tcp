package cn.nat.app.server.proxy.handlers;

import cn.nat.app.server.proxy.StreamChannelMapping;
import cn.nat.common.LoggerFactory;
import cn.nat.common.data.ConnectFrame;
import cn.nat.common.protocol.AbstractFrameHandler;
import cn.nat.common.protocol.Frame;
import io.netty.channel.Channel;
import org.slf4j.Logger;

/**
 * @author yang
 */
public class ConnectFrameHandler extends AbstractFrameHandler {
    Logger logger = LoggerFactory.getLogger("stream.connect");

    public ConnectFrameHandler() {
        super(ConnectFrame.FRAME_COMMAND);
    }

    @Override
    public void handle(Context ctx, Frame input) throws Exception {
        Channel channel = ctx.channel();

        ConnectFrame connect = new ConnectFrame();
        connect.readFrame(input);
        StreamChannelMapping.add(connect.tunnel(), connect.requestId(), channel);
        logger.info("Stream 通道注册: {}", connect.tunnel());
    }
}
