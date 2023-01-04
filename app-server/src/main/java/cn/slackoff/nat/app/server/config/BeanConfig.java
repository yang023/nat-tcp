package cn.slackoff.nat.app.server.config;

import cn.slackoff.nat.app.server.components.tunnels.TunnelGroupService;
import cn.slackoff.nat.app.server.domains.tunnels.JdbcTunnelGroupService;
import cn.slackoff.nat.app.server.domains.tunnels.TunnelRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yang
 */
@Configuration
public class BeanConfig {

    @Bean
    public TunnelGroupService clientRepository(TunnelRepository repository) {
        JdbcTunnelGroupService service = new JdbcTunnelGroupService();
        service.setRepository(repository);
        return service;
    }
}
