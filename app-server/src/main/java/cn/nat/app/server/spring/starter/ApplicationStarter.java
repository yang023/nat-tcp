package cn.nat.app.server.spring.starter;

import cn.nat.app.server.config.ServerConfig;
import cn.nat.common.container.Container;
import cn.nat.common.container.ContainerManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * @author yang
 */
@EnableConfigurationProperties(ServerConfig.class)
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