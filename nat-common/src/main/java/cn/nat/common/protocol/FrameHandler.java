package cn.nat.common.protocol;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @author yang
 */
public interface FrameHandler {

    boolean matches(String command);
    void handle(Context ctx, Frame input) throws Exception;

    interface Context {

        InetSocketAddress remote();

        void sendResponse(Frame output);

        Channel channel();
    }
}