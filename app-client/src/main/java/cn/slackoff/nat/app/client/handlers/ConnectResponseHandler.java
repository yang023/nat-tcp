package cn.slackoff.nat.app.client.handlers;

import cn.slackoff.nat.app.client.components.context.ClientContext;
import cn.slackoff.nat.app.client.components.context.ClientContextHolder;
import cn.slackoff.nat.core.data.ConnectRequest;
import cn.slackoff.nat.core.data.ConnectResponse;
import cn.slackoff.nat.core.execusion.FrameHandler;
import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.ContentType;
import cn.slackoff.nat.core.protocol.FrameInput;
import cn.slackoff.nat.core.protocol.FrameOutput;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yang
 */
@Slf4j
public class ConnectResponseHandler implements FrameHandler {

    @Override
    public void handleActive(FrameOutput output) throws Exception {
        ClientContext context = ClientContextHolder.getContext();
        ConnectRequest connectRequest = new ConnectRequest();
        connectRequest.setClientId(context.getClientId());
        connectRequest.setTunnels(context.getEnableTunnels());

        output.command(Command.CONNECT_REQUEST)
              .contentType(ContentType.JSON)
              .write(connectRequest);
    }

    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        ConnectResponse response = input.json(ConnectResponse.class);
        // 保存token
        ClientContext context = ClientContextHolder.getContext();
        if (!context.getClientId().equals(response.getClientId())) {
            log.error("Response another client-id.");
            System.exit(-1);
        }
    }
}
