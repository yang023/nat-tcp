package cn.nat.common.container;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yang
 */
public abstract class ContainerSupport<S extends ContainerSupport<S>> implements Container {

    private final Collection<Resource> resources = new CopyOnWriteArrayList<>();

    @Override
    public synchronized final void start(Context context) {
        checkIfPreset();

        Collection<Resource> res = start0(context);

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
        return print0();
    }

    protected void checkIfPreset() {
        //
    }

    protected abstract Collection<Resource> start0(Context context);
    protected abstract String print0();
}
