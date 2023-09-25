package cn.nat.common.container;

import java.util.Collection;

/**
 * @author yang
 */
public interface Container {

    void start(Context context);

    void stop();

    String printInfo();

    interface Context {

        default <C extends Container> C getContainer(Class<C> containerClass) {
            Collection<C> cs = getContainers(containerClass);
            return cs.isEmpty() ? null : cs.iterator().next();
        }

        <C extends Container> Collection<C> getContainers(Class<C> containerClass);

        <C extends Container> void addAndStart(C container);
    }
}