package cn.slackoff.nat.app.server.components.client;

import java.util.Optional;

/**
 * @author yang
 */
public interface ClientRepository {

    /**
     * 保存client信息
     */
    void saveClient(ClientInfo client);

    /**
     * 根据clientId获取client信息<br>
     */
    Optional<ClientInfo> findByClientId(String clientId);
}
