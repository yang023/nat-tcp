package cn.nat.app.server;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author yang
 */
@SpringBootApplication
public class Server {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(Server.class)
                .bannerMode(Banner.Mode.OFF).run(args);
    }
}