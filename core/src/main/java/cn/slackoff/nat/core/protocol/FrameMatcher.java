package cn.slackoff.nat.core.protocol;

/**
 * @author yang
 */
public interface FrameMatcher {

    boolean matches(Frame frame);
}
