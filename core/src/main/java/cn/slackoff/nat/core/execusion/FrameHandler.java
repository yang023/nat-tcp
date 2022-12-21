package cn.slackoff.nat.core.execusion;

import cn.slackoff.nat.core.protocol.FrameInput;
import cn.slackoff.nat.core.protocol.FrameOutput;

/**
 * @author yang
 */
public interface FrameHandler {

    default void handleActive(FrameOutput output) throws Exception {}

    void handleFrame(FrameInput input, FrameOutput output) throws Exception;
}
