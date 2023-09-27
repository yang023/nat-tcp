package cn.nat.common.container;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yang
 */
public abstract class ContainerSupport<S extends ContainerSupport<S>> implements Container {

    private final Collection<Resource> resources = new CopyOnWriteArrayList<>();

    private volatile boolean running;

    @Override
    public synchronized final void start(Context context) {
        if (running) {
            return;
        }

        checkIfPreset();

        Collection<Resource> res = start0(context);

        resources.addAll(res);

        running = true;
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

    @Override
    public boolean isRunning() {
        return this.running;
    }

    protected void checkIfPreset() {
        //
    }

    protected abstract Collection<Resource> start0(Context context);
    protected abstract String print0();
}
