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
    private static final DefaultErrorHandler DEFAULT_ERROR_HANDLER = new DefaultErrorHandler();
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
                        applyError(context, error);
                        return;
                    }

                    try {
                        handler.get().handle(context, deserialize);
                    } catch (Exception ex) {
                        ErrorFrame error = new ErrorFrame().message(ex.getMessage());
                        applyError(context, error);
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

    private void applyError(FrameHandler.Context context, ErrorFrame error) throws Exception {
        Frame frame = error.createFrame();
        Optional<FrameHandler> handler = registry.findHandler(frame);
        handler.orElse(DEFAULT_ERROR_HANDLER).handle(context, frame);
    }

    static class DefaultErrorHandler extends AbstractFrameHandler {
        DefaultErrorHandler() {
            super(ErrorFrame.FRAME_COMMAND);
        }

        @Override
        public void handle(Context ctx, Frame input) throws Exception {
            ErrorFrame frame = new ErrorFrame();
            frame.readFrame(input);
            System.err.printf("错误消息: %d - %s", frame.code(), frame.message());
        }
    }
}