package cn.slackoff.nat.app.server.domains.tunnels;

import cn.slackoff.nat.app.server.components.tunnels.TunnelGroup;
import cn.slackoff.nat.app.server.components.tunnels.TunnelGroupService;
import cn.slackoff.nat.core.data.TunnelInfo;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

/**
 * @author yang
 */
public class JdbcTunnelGroupService implements TunnelGroupService {

    @Setter
    private TunnelRepository repository;

    @Override
    public Optional<TunnelGroup> findByClientId(String clientId) {
        List<TunnelInfo> tunnels = repository.findAllByClientId(clientId).stream().map(it -> {
            TunnelInfo info = new TunnelInfo();
            info.setId(it.getId());
            info.setDomain(it.getProxyDomain());
            info.setEndpoint(it.getProxyEndpoint());
            return info;
        }).toList();
        if (tunnels.isEmpty()) {
            return Optional.empty();
        }
        TunnelGroup tunnelGroup = new TunnelGroup();
        tunnelGroup.setClientId(clientId);
        tunnelGroup.setTunnels(tunnels);
        return Optional.of(tunnelGroup);
    }
}
