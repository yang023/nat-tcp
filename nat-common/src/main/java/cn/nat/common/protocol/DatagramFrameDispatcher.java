package cn.nat.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

/**
 * @author yang
 */
public final class DatagramFrameDispatcher extends FrameDispatcher<DatagramPacket> {

    public DatagramFrameDispatcher(FrameHandlerRegistry registry) {
        super(registry);
    }

    @Override
    protected ByteBuf provide(DatagramPacket msg) throws Exception {
        return msg.content();
    }

    @Override
    protected FrameHandler.Context createContext(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        return new ContextImpl(ctx, msg.sender());
    }

    static class ContextImpl implements FrameHandler.Context {
        private final ChannelHandlerContext ctx;
        private final InetSocketAddress target;

        ContextImpl(ChannelHandlerContext ctx, InetSocketAddress target) {
            this.ctx = ctx;
            this.target = target;
        }

        @Override
        public InetSocketAddress remote() {
            return target;
        }

        @Override
        public void sendResponse(Frame output) {
            ByteBuf buf = output.serialize();
            DatagramPacket packet = new DatagramPacket(buf, target);
            ctx.writeAndFlush(packet);
        }

        @Override
        public Channel channel() {
            return ctx.channel();
        }
    }
}