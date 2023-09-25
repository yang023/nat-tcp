package cn.nat.app.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yang
 */
@Getter
@Setter
@ConfigurationProperties("nat.server")
public class ServerConfig {

    private static final Gateway EMPTY_PROXY = new Gateway();

    private int port = 9527;
    private int streamPort = 8000;

    private Map<String, Gateway> gateway = new HashMap<>();

    @Getter
    @Setter
    public static class Gateway {
        private int port;

        private Map<String, String> properties = new HashMap<>();

        public int getPort() {
            if (port <= 0) {
                throw new IllegalArgumentException("Proxy protocol's port is 0");
            }
            return port;
        }

        public int getPort(int defaultPort) {
            if (port <= 0) {
                return defaultPort;
            }
            return port;
        }
    }

    public Gateway getGateway(String name) {
        return gateway.computeIfAbsent(name, __ -> EMPTY_PROXY);
    }
}
