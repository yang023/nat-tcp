package cn.slackoff.nat.app.server.handlers;

import cn.slackoff.nat.app.server.components.registration.ProxyErrorConsumer;
import cn.slackoff.nat.app.server.components.registration.Registration;
import cn.slackoff.nat.app.server.components.registration.RegistrationManager;
import cn.slackoff.nat.core.data.ErrorResponse;
import cn.slackoff.nat.core.execusion.FrameHandler;
import cn.slackoff.nat.core.protocol.FrameInput;
import cn.slackoff.nat.core.protocol.FrameOutput;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author yang
 */
public class ErrorResponseHandler implements FrameHandler {

    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        ErrorResponse response = input.json(ErrorResponse.class);
        String clientId = input.headers().get("clientId");
        if (!StringUtils.hasText(clientId)) {
            throw new RuntimeException(response.getMessage());
        }

        String channelId = input.headers().get("channelId");
        Registration registration = RegistrationManager.findRegistrationByClientId(clientId)
                                                       .orElseThrow();

        Optional<ProxyErrorConsumer> consumerOptional = registration.findErrorConsumer(channelId);
        if (consumerOptional.isEmpty()) {
            throw new RuntimeException(response.getMessage());
        }

        consumerOptional.get().accetp(response);
    }
}
