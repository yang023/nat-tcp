package cn.slackoff.nat.app.server.components.client;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yang
 */
public class InMemoryClientRepository implements ClientRepository {
    private final ConcurrentMap<String, ClientInfo> clientMap = new ConcurrentHashMap<>();


    @Override
    public void saveClient(ClientInfo client) {
        clientMap.put(client.getId(), client);
    }

    @Override
    public Optional<ClientInfo> findByClientId(String clientId) {
        return Optional.ofNullable(clientMap.getOrDefault(clientId, null));
    }
}
