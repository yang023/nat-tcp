package cn.nat.common.container;

import cn.nat.common.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * @author yang
 */
public final class ContainerManager {
    private static final Logger logger = LoggerFactory.getLogger("container");
    private final Collection<Container> containers = new CopyOnWriteArrayList<>();

    private volatile boolean started;

    private volatile ContextImpl ctx;

    public void add(Container container) {
        if (started) {
            throw new IllegalStateException("服务已启动");
        }
        if (container != null) {
            synchronized (containers) {
                this.containers.add(container);
            }
        }
    }

    public void add(Supplier<Container> containerSupplier) {
        if (started) {
            logger.error("服务已启动, 不能添加服务容器");
            throw new IllegalStateException("服务已启动");
        }
        if (containerSupplier != null) {
            add(containerSupplier.get());
        } else {
            logger.warn("添加服务容器为null");
        }
    }

    public void start() {
        if (started) {
            logger.error("服务已启动, 不能再次启动");
            throw new IllegalStateException("容器已启动");
        }

        synchronized (containers) {
            ctx = new ContextImpl(this.containers);

            synchronized (containers) {
                for (Container container : this.containers) {
                    container.start(ctx);
                    logger.info("容器已启动: {}", container.printInfo());
                }
            }

            started = true;
        }
    }

    public void stop() {
        if (!started) {
            return;
        }
        synchronized (containers) {
            for (Container container : this.containers) {
                container.stop();
                logger.info("容器已停止: {}", container.printInfo());
            }

            started = false;
        }
    }

    static class ContextImpl implements Container.Context {
        private final Collection<Container> containers;

        private ContextImpl(Collection<Container> containers) {
            this.containers = containers;
        }


        @Override
        public <C extends Container> Collection<C> getContainers(Class<C> containerClass) {
            List<C> cs = new ArrayList<>();
            for (Container container : containers) {
                if (container.getClass().isAssignableFrom(containerClass)) {
                    //noinspection unchecked
                    cs.add((C) container);
                }
            }

            return Collections.unmodifiableCollection(cs);
        }

        @Override
        public <C extends Container> void addAndStart(C container) {
            synchronized (this.containers) {
                container.start(this);
                this.containers.add(container);
                logger.info("容器已添加并启动: {}", container.printInfo());
            }
        }
    }
}