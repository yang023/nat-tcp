package cn.nat.app.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yang
 */
@Getter
@Setter
@ConfigurationProperties(ServerConfig.CONFIG_PREFIX)
public class ServerConfig implements InitializingBean {
    public static final String CONFIG_PREFIX = "nat";

    private int port = 9527;
    private int streamPort = 8000;

    private Map<GatewayType, Gateway> gateway = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(this.port > 0, errorProp("port", this.port));
        Assert.isTrue(this.streamPort > 0, errorProp("stream-port", this.streamPort));

        for (Map.Entry<GatewayType, Gateway> entry : this.gateway.entrySet()) {
            GatewayType name = entry.getKey();
            Gateway value = entry.getValue();
            Assert.isTrue(value.port > 0, errorProp("gateway.%s.port".formatted(name), value.port));
        }
    }

    public Gateway getGateway(String name) {
        GatewayType type = GatewayType.valueOf(name);
        return gateway.computeIfAbsent(type, __ -> new Gateway());
    }

    private static String errorProp(String name, Object value) {
        return "错误参数: %s.%s -> %s".formatted(CONFIG_PREFIX, name, value);
    }

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
                port = defaultPort;
                return defaultPort;
            }
            return port;
        }
    }

    public enum GatewayType {
        http
    }
}
