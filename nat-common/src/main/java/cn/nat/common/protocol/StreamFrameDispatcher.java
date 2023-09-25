package cn.nat.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @author yang
 */
public final class StreamFrameDispatcher extends FrameDispatcher<ByteBuf>  {
    public StreamFrameDispatcher(FrameHandlerRegistry registry) {
        super(registry);
    }

    @Override
    protected ByteBuf provide(ByteBuf msg) throws Exception {
        return msg;
    }

    @Override
    protected FrameHandler.Context createContext(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        return new ContextImpl(ctx);
    }

    static class ContextImpl implements FrameHandler.Context {
        private final ChannelHandlerContext ctx;

        ContextImpl(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public InetSocketAddress remote() {
            return (InetSocketAddress) ctx.channel().remoteAddress();
        }

        @Override
        public void sendResponse(Frame output) {
            ByteBuf buf = output.serialize();
            ctx.writeAndFlush(buf);
        }

        @Override
        public Channel channel() {
            return ctx.channel();
        }
    }
}