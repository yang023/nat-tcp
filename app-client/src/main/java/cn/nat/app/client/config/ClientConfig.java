package cn.nat.app.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yang
 */
@Getter
@Setter
@ConfigurationProperties(ClientConfig.CONFIG_PREFIX)
public class ClientConfig implements InitializingBean {
    public static final String CONFIG_PREFIX = "nat";

    private String clientId;

    private Server server = new Server();

    private List<Tunnel> tunnels = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.clientId, errorProp("client-id", this.clientId));
        Assert.hasText(this.server.host, errorProp("server.host", this.server.host));
        Assert.isTrue(this.server.port > 0, errorProp("server.port", this.server.port));
        Assert.notEmpty(this.tunnels, errorProp("tunnels", this.tunnels));
        for (int i = 0; i < this.tunnels.size(); i++) {
            Tunnel tunnel = this.tunnels.get(i);
            Assert.hasText(tunnel.name, errorProp("tunnels[%d].name".formatted(i), tunnel.name));
            Assert.hasText(tunnel.host, errorProp("tunnels[%d].host".formatted(i), tunnel.host));
            Assert.isTrue(tunnel.port > 0, errorProp("tunnels[%d].port".formatted(i), tunnel.port));
        }
    }

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

    private static String errorProp(String name, Object value) {
        return "错误参数: %s.%s -> %s".formatted(CONFIG_PREFIX, name, value);
    }
}
