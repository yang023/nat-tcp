package cn.nat.app.client;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author yang
 */
@SpringBootApplication
public class Client {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder()
                .sources(Client.class)
                .bannerMode(Banner.Mode.OFF).run(args);
    }
}