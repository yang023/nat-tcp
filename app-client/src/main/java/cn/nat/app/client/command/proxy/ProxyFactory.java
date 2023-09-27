package cn.nat.app.client.command.proxy;

import cn.nat.app.client.data.ProxyTunnel;

/**
 * @author yang
 */
public final class ProxyFactory {

    static ProxyFactory instance;

    final String streamHost;
    final int streamPort;
    final long readRate;
    final long writeRate;

    private ProxyFactory(String streamHost, int streamPort, long readRate, long writeRate) {
        this.streamHost = streamHost;
        this.streamPort = streamPort;
        this.readRate = readRate;
        this.writeRate = writeRate;
    }

    public static void createProxy(ProxyTunnel tunnel, String requestId) {
        checkCreated();

        new ProxyClient(requestId).config(tunnel).start(null);
    }

    static void checkCreated() {
        if (instance == null) {
            throw new IllegalStateException("ProxyFactory 实例未创建");
        }
    }

    public static class Builder {
        private String streamHost;
        private int streamPort;
        private long readRate;
        private long writeRate;

        public Builder() {
            if (instance != null) {
                throw new IllegalStateException("已创建 TransportFactory 实例");
            }
        }

        public Builder streamHost(String streamHost) {
            this.streamHost = streamHost;
            return this;
        }

        public Builder streamPort(int streamPort) {
            this.streamPort = streamPort;
            return this;
        }

        public Builder readRate(long readRate) {
            this.readRate = readRate;
            return this;
        }

        public Builder writeRate(long writeRate) {
            this.writeRate = writeRate;
            return this;
        }

        public void build() {
            instance = new ProxyFactory(
                    streamHost, streamPort, readRate, writeRate);
        }
    }
}
