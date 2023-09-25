package cn.nat.app.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yang
 */
@Getter
@Setter
@ConfigurationProperties("nat.client")
public class ClientConfig {

    private String clientId;

    private Server server = new Server();

    private List<Tunnel> tunnels = new ArrayList<>();

    @Getter
    @Setter
    public static class Server {
        private String host;
        private int port = 9527;
    }

    @Getter
    @Setter
    public static class Tunnel {
        private String name;
        private String host = "127.0.0.1"; // 默认就是本机
        private int port;
    }
}
