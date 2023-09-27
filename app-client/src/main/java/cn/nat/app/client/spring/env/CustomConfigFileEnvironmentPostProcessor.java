package cn.nat.app.client.spring.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author yang
 */
public class CustomConfigFileEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final String EXTERNAL_CONFIG_FILE_PROPERTY = "tunnel-location";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String property = environment.getProperty(EXTERNAL_CONFIG_FILE_PROPERTY);
        if (!StringUtils.hasText(property)) {
            return;
        }

        Path configPath = Paths.get(property);
        if (!configPath.toFile().exists()) {
            return;
        }

        try {
            Path realPath = configPath.toAbsolutePath().toRealPath();
            System.out.printf("加载外部配置文件: %s%n", realPath);

            FileSystemResource resource = new FileSystemResource(realPath);
            List<PropertySource<?>> external = new YamlPropertySourceLoader().load("external", resource);

            MutablePropertySources propertySources = environment.getPropertySources();
            for (PropertySource<?> propertySource : external) {
                propertySources.addLast(propertySource);
            }

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
