package cn.slackoff.nat.app.client.components.context;

import cn.slackoff.nat.core.boot.CustomizeNettyClient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author yang
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ClientContext {
    private final String clientId;
    private final List<String> enableTunnels;
    @Getter(AccessLevel.PRIVATE)
    private final Map<String, CustomizeNettyClient> cachedClients = new HashMap<>();

    public void addPreparedClient(String endpoint, CustomizeNettyClient client) {
        synchronized (cachedClients) {
            cachedClients.put(endpoint, client);
        }
    }

    public Optional<CustomizeNettyClient> getPreparedClient(String endpoint) {
        synchronized (cachedClients) {
            return Optional.ofNullable(cachedClients.getOrDefault(endpoint, null));
        }
    }
}
