package cn.slackoff.nat.app.client.components;

import cn.slackoff.nat.core.boot.CustomizeNettyClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author yang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProxyServerPool {

    public static CustomizeNettyClient getClient() {
        try {
            return PoolHolder.POOL.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class PoolHolder {
        private static final GenericObjectPool<CustomizeNettyClient> POOL;

        static {
            GenericObjectPoolConfig<CustomizeNettyClient> config = new GenericObjectPoolConfig<>();
            POOL = new GenericObjectPool<>(new Factory(), config);
        }
    }

    private static class Factory extends BasePooledObjectFactory<CustomizeNettyClient> {

        @Override
        public CustomizeNettyClient create() throws Exception {
            CustomizeNettyClient client = new CustomizeNettyClient();
            client.onClosed(e -> {
                if (e.isSuccess()) {
                    PoolHolder.POOL.returnObject(client);
                }
            });
            return client;
        }

        @Override
        public PooledObject<CustomizeNettyClient> wrap(CustomizeNettyClient obj) {
            return new DefaultPooledObject<>(obj);
        }
    }
}
