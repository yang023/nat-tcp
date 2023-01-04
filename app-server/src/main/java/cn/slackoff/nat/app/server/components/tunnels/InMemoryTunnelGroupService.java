package cn.slackoff.nat.app.server.components.tunnels;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yang
 */
@Deprecated
public class InMemoryTunnelGroupService implements TunnelGroupService {
    private final ConcurrentMap<String, TunnelGroup> clientMap = new ConcurrentHashMap<>();

    public InMemoryTunnelGroupService(TunnelGroup... groups) {
        for (TunnelGroup group : groups) {
            clientMap.put(group.getClientId(), group);
        }
    }

    @Override
    public Optional<TunnelGroup> findByClientId(String clientId) {
        return Optional.ofNullable(clientMap.getOrDefault(clientId, null));
    }
}
