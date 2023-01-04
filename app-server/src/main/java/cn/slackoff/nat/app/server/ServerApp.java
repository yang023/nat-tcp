package cn.slackoff.nat.app.server;

import cn.slackoff.nat.app.server.config.ServerProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author yang
 */
@EnableConfigurationProperties(ServerProps.class)
@SpringBootApplication
public class ServerApp {

    public static final String CONFIG_PREFIX = "nat.server";

    public static void main(String[] args) {
        SpringApplication.run(ServerApp.class, args);
    }
}
