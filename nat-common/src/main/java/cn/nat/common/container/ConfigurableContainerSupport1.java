package cn.nat.common.container;

import java.util.Collection;

/**
 * @author yang
 */
@SuppressWarnings("unchecked")
public abstract class ConfigurableContainerSupport1<C, S extends ConfigurableContainerSupport1<C, S>> extends ContainerSupport<S> {

    private C config;

    public S config(C config) {
        this.config = config;
        return (S) this;
    }

    @Override
    public synchronized final Collection<Resource> start0(Context context) {
        checkIfPreset();

        return start(context, config);
    }

    @Override
    protected String print0() {
        return print(config);
    }

    protected final void checkIfPreset() {
        checkIfPreset(config);
    }

    protected void checkIfPreset(C config) {
        //
    }

    protected abstract Collection<Resource> start(Context context, C config);

    protected abstract String print(C config);
}
