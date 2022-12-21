package cn.slackoff.nat.core.protocol.matcher;

import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.Frame;
import cn.slackoff.nat.core.protocol.FrameMatcher;

/**
 * @author yang
 */
final class CommandMatcher implements FrameMatcher {
    private final Command command;

    CommandMatcher(Command command) {
        this.command = command;
    }

    @Override
    public boolean matches(Frame frame) {
        return frame.command().equals(command);
    }
}
