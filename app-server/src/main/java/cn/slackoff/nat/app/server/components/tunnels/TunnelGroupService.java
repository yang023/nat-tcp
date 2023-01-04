package cn.slackoff.nat.app.server.components.tunnels;

import java.util.Optional;

/**
 * @author yang
 */
public interface TunnelGroupService {

    /**
     * 根据clientId获取client信息<br>
     */
    Optional<TunnelGroup> findByClientId(String clientId);
}
