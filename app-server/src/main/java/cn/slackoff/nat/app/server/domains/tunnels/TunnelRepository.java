package cn.slackoff.nat.app.server.domains.tunnels;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yang
 */
@Repository
public interface TunnelRepository extends CrudRepository<TunnelEntity, String> {

    List<TunnelEntity> findAllByClientId(String clientId);
}
