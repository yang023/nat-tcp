package cn.slackoff.nat.core.protocol.matcher;

import cn.slackoff.nat.core.protocol.ContentType;
import cn.slackoff.nat.core.protocol.Frame;
import cn.slackoff.nat.core.protocol.FrameMatcher;

/**
 * @author yang
 */
final class ContentTypeMatcher implements FrameMatcher {
    private final ContentType contentType;

    ContentTypeMatcher(ContentType contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean matches(Frame frame) {
        return frame.contentType().equals(contentType);
    }
}
