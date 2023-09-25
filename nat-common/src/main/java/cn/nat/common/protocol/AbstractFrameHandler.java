package cn.nat.common.protocol;

/**
 * @author yang
 */
public abstract class AbstractFrameHandler implements FrameHandler {
    private final String command;

    public AbstractFrameHandler(String command) {
        this.command = command;
    }

    @Override
    public final boolean matches(String command) {
        return this.command.equals(command);
    }
}