package cn.slackoff.nat.app.server.handlers;

import cn.slackoff.nat.app.server.components.registration.RegistrationManager;
import cn.slackoff.nat.app.server.components.tunnels.TunnelGroup;
import cn.slackoff.nat.app.server.components.tunnels.TunnelGroupService;
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
    private TunnelGroupService tunnelGroupService;

    @Override
    public void handleFrame(FrameInput input, FrameOutput output) throws Exception {
        ConnectRequest request = input.json(ConnectRequest.class);
        ErrorResponse errorResponse;

        // 1. 校验clientId
        Optional<TunnelGroup> groupInfo = tunnelGroupService.findByClientId(request.getClientId());
        if (groupInfo.isEmpty()) {
            ErrorResponse.create(Command.CONNECT_REQUEST, "Not found client-id").write(output);
            return;
        }
        TunnelGroup tunnelGroup = groupInfo.get();

        // 2. 校验tunnels
        errorResponse = validClientTunnels(tunnelGroup, request);
        if (errorResponse != null) {
            errorResponse.write(output);
            return;
        }

        ConnectResponse connectResponse = new ConnectResponse();
        connectResponse.setClientId(request.getClientId());

        output.command(Command.CONNECT_RESPONSE)
              .contentType(ContentType.JSON)
              .write(connectResponse);

        RegistrationManager.create(tunnelGroup, input.channel());
    }

    private ErrorResponse validClientTunnels(TunnelGroup client, ConnectRequest request) {
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
