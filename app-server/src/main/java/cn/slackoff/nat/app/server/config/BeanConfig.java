package cn.slackoff.nat.app.server.config;

import cn.slackoff.nat.app.server.components.client.ClientRepository;
import cn.slackoff.nat.app.server.components.client.InMemoryClientRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author yang
 */
@Configuration
@Import({BeanConfig.ClientRepositoryConfig.class})
public class BeanConfig {

    static class ClientRepositoryConfig {

        @Bean
        @ConditionalOnMissingBean(ClientRepository.class)
        public ClientRepository clientRepository() {
            return new InMemoryClientRepository();
        }
    }
}
