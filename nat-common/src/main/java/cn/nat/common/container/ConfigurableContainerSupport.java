package cn.nat.common.container;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yang
 */
@SuppressWarnings("unchecked")
public abstract class ConfigurableContainerSupport<C, S extends ConfigurableContainerSupport<C, S>> implements Container {

    private final Collection<Resource> resources = new CopyOnWriteArrayList<>();

    private C config;

    public S config(C config) {
        this.config = config;
        return (S) this;
    }

    @Override
    public synchronized void start(Context context) {
        checkIfPreset(config);

        Collection<Resource> res = start(context, config);

        resources.addAll(res);
    }

    @Override
    public synchronized final void stop() {
        for (Resource resource : resources) {
            resource.release();
        }
    }

    @Override
    public final String printInfo() {
        return print(config);
    }

    protected void checkIfPreset(C config) {
        //
    }

    protected abstract Collection<Resource> start(Context context, C config);
    protected abstract String print(C config);
}
