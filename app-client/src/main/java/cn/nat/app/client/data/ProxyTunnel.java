package cn.nat.app.client.data;

/**
 * @author yang
 */
public class ProxyTunnel {
    private static final int MIN_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;

    private String name;

    private String host;
    private int port;

    private int poolSize;

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

    public int poolSize() {
        int size = Math.max(poolSize, MIN_POOL_SIZE);
        return Math.min(size, MAX_POOL_SIZE);
    }

    public ProxyTunnel poolSize(int poolSize) {
        this.poolSize = poolSize;
        return this;
    }
}