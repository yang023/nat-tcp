package cn.slackoff.nat.app.server.handlers;

import cn.slackoff.nat.app.server.components.client.ClientInfo;
import cn.slackoff.nat.app.server.components.client.ClientRepository;
import cn.slackoff.nat.app.server.components.registration.RegistrationManager;
import cn.slackoff.nat.core.data.ConnectRequest;
import cn.slackoff.nat.core.data.ConnectResponse;
import cn.slackoff.nat.core.data.ErrorResponse;
import cn.slackoff.nat.core.data.TunnelInfo;
import cn.slackoff.nat.core.execusion.FrameHandler;
import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.ContentType;
import cn.slackoff.nat.core.protocol.FrameInput;
import cn.slackoff.nat.core.protocol.FrameOutput;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * 接收{@link cn.slackoff.nat.core.protocol.Command#CONNECT_REQUEST} 消息<br>
 *
 * @author yang
 */
@Slf4j
public class ConnectRequestHandler implements FrameHandler {

    @Setter
    private ClientRepository clientRepository;

    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        ConnectRequest request = input.json(ConnectRequest.class);
        ErrorResponse errorResponse;

        // 1. 校验clientId
        Optional<ClientInfo> clientInfo = clientRepository.findByClientId(request.getClientId());
        if (clientInfo.isEmpty()) {
            ErrorResponse.create(Command.PREPARE_REQUEST, "Not found client-id").write(output);
            return;
        }
        ClientInfo client = clientInfo.get();

        // 2. 校验tunnels
        errorResponse = validClientTunnels(client, request);
        if (errorResponse != null) {
            errorResponse.write(output);
            return;
        }

        ConnectResponse connectResponse = new ConnectResponse();
        connectResponse.setClientId(request.getClientId());

        output.command(Command.CONNECT_RESPONSE)
              .contentType(ContentType.JSON)
              .write(connectResponse);

        RegistrationManager.create(client, input.channel());
    }

    private ErrorResponse validClientTunnels(ClientInfo client, ConnectRequest request) {
        if (request.getTunnels().isEmpty()) {
            return ErrorResponse.create(Command.CONNECT_REQUEST, "Not found client-id");
        }
        // 申请了不存在的tunnel则报错, 即->requestTunnels是configuredTunnels的真子集或两集合相等
        List<String> configuredTunnels = client.getTunnels().stream()
                                               .map(TunnelInfo::getId).toList();
        List<String> requestTunnels = request.getTunnels();
        for (String tunnel : requestTunnels) {
            if (!configuredTunnels.contains(tunnel)) {
                return ErrorResponse.create(Command.CONNECT_REQUEST, "No managed tunnel-id[" + tunnel + "]");
            }
        }
        return null;
    }
}
