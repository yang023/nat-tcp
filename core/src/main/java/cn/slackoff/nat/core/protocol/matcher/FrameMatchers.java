package cn.slackoff.nat.core.protocol.matcher;

import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.ContentType;
import cn.slackoff.nat.core.protocol.FrameMatcher;

/**
 * @author yang
 */
public final class FrameMatchers {
    private FrameMatchers() {}

    public static FrameMatcher ofCommand(Command command) {
        return new CommandMatcher(command);
    }

    public static FrameMatcher ofContentType(ContentType contentType) {
        return new ContentTypeMatcher(contentType);
    }

    public static FrameMatcher orMatcher(FrameMatcher... matchers) {
        return frame -> {
            for (FrameMatcher matcher : matchers) {
                if (matcher.matches(frame)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static FrameMatcher andMatcher(FrameMatcher... matchers) {
        return frame -> {
            for (FrameMatcher matcher : matchers) {
                if (matcher.matches(frame)) {
                    return false;
                }
            }
            return true;
        };
    }
}
