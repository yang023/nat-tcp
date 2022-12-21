package cn.slackoff.nat.core.execusion;

import cn.slackoff.nat.core.tools.OriginalBytesHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author yang
 */
public class OriginalBytesHolderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf buf) {
            ByteBuf bytes = ByteBufAllocator.DEFAULT.directBuffer().writeBytes(buf);
            buf.resetReaderIndex();
            OriginalBytesHolder.saveChannelBytes(ctx.channel(), bytes);
        }
        super.channelRead(ctx, msg);
    }
}
