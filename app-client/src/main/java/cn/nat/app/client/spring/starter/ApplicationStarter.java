package cn.nat.app.client.spring.starter;

import cn.nat.app.client.config.ClientConfig;
import cn.nat.common.container.ContainerManager;
import cn.nat.common.container.Container;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * @author yang
 */
@EnableConfigurationProperties(ClientConfig.class)
@Configuration
public class ApplicationStarter {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ContainerManager containerManager(Collection<Container> containers) {
        ContainerManager manager = new ContainerManager();
        for (Container container : containers) {
            manager.add(container);
        }
        return manager;
    }
}