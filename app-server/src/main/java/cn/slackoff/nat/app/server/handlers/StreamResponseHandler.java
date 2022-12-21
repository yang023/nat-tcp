package cn.slackoff.nat.app.server.handlers;

import cn.slackoff.nat.app.server.components.registration.Registration;
import cn.slackoff.nat.app.server.components.registration.RegistrationManager;
import cn.slackoff.nat.core.execusion.FrameHandler;
import cn.slackoff.nat.core.protocol.FrameInput;
import cn.slackoff.nat.core.protocol.FrameOutput;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * @author yang
 */
public class StreamResponseHandler implements FrameHandler {
    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        ByteBuf stream = input.input();
        String clientId = input.headers().get("clientId");
        String channelId = input.headers().get("channelId");
        Registration registration = RegistrationManager.findRegistrationByClientId(clientId).orElseThrow();
        Channel channel = registration.findChannel(channelId).orElseThrow();
        channel.writeAndFlush(stream).addListener(ChannelFutureListener.CLOSE);
    }
}
