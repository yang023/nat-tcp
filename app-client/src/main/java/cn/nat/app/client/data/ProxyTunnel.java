package cn.nat.app.client.data;

/**
 * @author yang
 */
public class ProxyTunnel {
    private String name;

    private String host;
    private int port;

    public String name() {
        return name;
    }

    public ProxyTunnel name(String name) {
        this.name = name;
        return this;
    }

    public String host() {
        return host;
    }

    public ProxyTunnel host(String host) {
        this.host = host;
        return this;
    }

    public int port() {
        return port;
    }

    public ProxyTunnel port(int port) {
        this.port = port;
        return this;
    }
}