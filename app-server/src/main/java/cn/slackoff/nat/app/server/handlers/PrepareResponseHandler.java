package cn.slackoff.nat.app.server.handlers;

import cn.slackoff.nat.app.server.components.registration.Registration;
import cn.slackoff.nat.app.server.components.registration.RegistrationManager;
import cn.slackoff.nat.core.data.PrepareResponse;
import cn.slackoff.nat.core.execusion.FrameHandler;
import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.ContentType;
import cn.slackoff.nat.core.protocol.FrameInput;
import cn.slackoff.nat.core.protocol.FrameOutput;
import cn.slackoff.nat.core.tools.OriginalBytesHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * @author yang
 */
public class PrepareResponseHandler implements FrameHandler {
    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        PrepareResponse response = input.json(PrepareResponse.class);

        Registration registration = RegistrationManager.findRegistrationByClientId(response.getClientId())
                                                       .orElseThrow();
        // 代理服务保存的原始报文
        Channel channel = registration.findChannel(response.getChannelId()).orElseThrow();
        ByteBuf channelBytes = OriginalBytesHolder.findChannelBytes(channel);

        output.command(Command.STREAM_REQUEST)
              .contentType(ContentType.STREAM)
              .headers(h -> {
                  h.set("channelId", response.getChannelId());
                  h.set("endpoint", response.getEndpoint());
              })
              .write(channelBytes);
    }
}
