package cn.slackoff.nat.app.client.handlers;

import cn.slackoff.nat.core.data.ErrorResponse;
import cn.slackoff.nat.core.execusion.FrameHandler;
import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.FrameInput;
import cn.slackoff.nat.core.protocol.FrameOutput;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yang
 */
@Slf4j
public class ErrorResponseClientHandler implements FrameHandler {

    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        ErrorResponse response = input.json(ErrorResponse.class);
        log.error(response.getMessage());
        if (response.getFrom().equals(Command.CONNECT_REQUEST)) {
            System.exit(-1);
        }
    }
}
