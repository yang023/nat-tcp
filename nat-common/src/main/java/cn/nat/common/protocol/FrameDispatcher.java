package cn.nat.common.protocol;

import cn.nat.common.data.ErrorFrame;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;

import java.util.Optional;

/**
 * @author yang
 */
abstract class FrameDispatcher<T> extends ChannelInboundHandlerAdapter {
    private final TypeParameterMatcher matcher;
    private final FrameHandlerRegistry registry;

    protected FrameDispatcher(FrameHandlerRegistry registry) {
        this.matcher = TypeParameterMatcher.find(this, FrameDispatcher.class, "T");
        this.registry = registry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
            if (matcher.match(msg)) {
                //noinspection unchecked
                T t = (T) msg;
                ByteBuf provided = provide(t);
                Frame deserialize = Frame.deserialize(provided);
                if (deserialize == null) {
                    release = false;
                    ctx.fireChannelRead(msg);
                } else {
                    FrameHandler.Context context = createContext(ctx, t);
                    Optional<FrameHandler> handler = registry.findHandler(deserialize);

                    if (handler.isEmpty()) {
                        ErrorFrame error = new ErrorFrame().message(
                                "Error command: [%s]".formatted(deserialize.command()));
                        Frame errorFrame = error.createFrame();
                        context.sendResponse(errorFrame);
                        return;
                    }

                    try {
                        handler.get().handle(context, deserialize);
                    } catch (Exception ex) {
                        ErrorFrame error = new ErrorFrame().message(ex.getMessage());
                        Frame errorFrame = error.createFrame();
                        context.sendResponse(errorFrame);
                    }
                }
            } else {
                release = false;
                ctx.fireChannelRead(msg);
            }
        } finally {
            if (release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }

    protected abstract ByteBuf provide(T msg) throws Exception;

    protected abstract FrameHandler.Context createContext(ChannelHandlerContext ctx, T msg) throws Exception;
}