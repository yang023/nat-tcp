package cn.nat.common.container;

import java.util.Collection;

/**
 * @author yang
 */
@SuppressWarnings("unchecked")
public abstract class ConfigurableContainerSupport<C, S extends ConfigurableContainerSupport<C, S>> extends ContainerSupport<S> {

    private C config;

    public S config(C config) {
        this.config = config;
        return (S) this;
    }

    @Override
    protected synchronized final Collection<Resource> start0(Context context) {
        checkIfPreset(config);

        return start(context, config);
    }

    @Override
    protected String print0() {
        return print(config);
    }

    protected void checkIfPreset(C config) {
        //
    }

    protected abstract Collection<Resource> start(Context context, C config);
    protected abstract String print(C config);
}
