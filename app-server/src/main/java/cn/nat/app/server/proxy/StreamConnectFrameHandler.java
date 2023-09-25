package cn.nat.app.server.proxy;

import cn.nat.common.data.ConnectFrame;
import cn.nat.common.protocol.AbstractFrameHandler;
import cn.nat.common.protocol.Frame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;

import java.util.function.Function;

/**
 * @author yang
 */
public class StreamConnectFrameHandler extends AbstractFrameHandler {

    private final Function<String, ChannelGroup> channelGroupFinder;
    public StreamConnectFrameHandler(Function<String, ChannelGroup> channelGroupFinder) {
        super(ConnectFrame.FRAME_COMMAND);
        this.channelGroupFinder = channelGroupFinder;
    }

    @Override
    public void handle(Context ctx, Frame input) throws Exception {
        ConnectFrame connectFrame = new ConnectFrame();
        connectFrame.readFrame(input);

        channelGroupFinder.apply(connectFrame.tunnel()).add(ctx.channel());

        ChannelPipeline pipeline = ctx.channel().pipeline();
        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ProxyChannelMapping.triggerStream(ctx.channel(), msg);
            }
        });
    }
}