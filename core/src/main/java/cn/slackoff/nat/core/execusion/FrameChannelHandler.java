package cn.slackoff.nat.core.execusion;

import cn.slackoff.nat.core.execusion.impl.DefaultFrameInput;
import cn.slackoff.nat.core.execusion.impl.DefaultFrameOutput;
import cn.slackoff.nat.core.protocol.Frame;
import cn.slackoff.nat.core.protocol.FrameMatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author yang
 */
@Slf4j
public class FrameChannelHandler extends ChannelInboundHandlerAdapter {

    private final Map<FrameMatcher, FrameHandler> handlerMap;

    public FrameChannelHandler(Consumer<Map<FrameMatcher, FrameHandler>> handlerMapConsumer) {
        Assert.notNull(handlerMapConsumer, "Handler(s) customizer cannot be null.");
        Map<FrameMatcher, FrameHandler> map = new HashMap<>();
        handlerMapConsumer.accept(map);
        this.handlerMap = Collections.unmodifiableMap(map);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (FrameHandler handler : handlerMap.values()) {
            handler.handleActive(new DefaultFrameOutput(ctx.channel()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Frame frame)) {
            super.channelRead(ctx, msg);
            return;
        }

        Set<Map.Entry<FrameMatcher, FrameHandler>> entries = handlerMap.entrySet();
        for (Map.Entry<FrameMatcher, FrameHandler> entry : entries) {
            FrameMatcher matcher = entry.getKey();
            if (matcher.matches(frame)) {
                FrameHandler handler = entry.getValue();
                handler.handleFrame(new DefaultFrameInput(ctx.channel(), frame), new DefaultFrameOutput(ctx.channel()));
                return;
            }
        }

        log.warn("Not found handler for command: {}", frame.command());
        super.channelRead(ctx, msg);
    }
}
