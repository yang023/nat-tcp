package cn.nat.common.protocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author yang
 */
public final class FrameHandlerRegistry {
    private final Collection<FrameHandler> handlers = new ArrayList<>();

    public void register(FrameHandler handler) {
        if (handler == null) {
            return;
        }
        this.handlers.add(handler);
    }

    public void register(Supplier<FrameHandler> handlerSupplier) {
        if (handlerSupplier == null) {
            return;
        }
        this.handlers.add(handlerSupplier.get());
    }

    Optional<FrameHandler> findHandler(Frame frame) {
        if (handlers.isEmpty()) {
            return Optional.empty();
        }

        for (FrameHandler handler : handlers) {
            if (handler.matches(frame.command())) {
                return Optional.of(handler);
            }
        }

        return Optional.empty();
    }
}