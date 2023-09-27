package cn.nat.app.server.proxy;


import cn.nat.common.data.ErrorFrame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.io.IOException;

/**
 * @author yang
 */
public interface ProxyMessageHandler {

    boolean isFirstRequest(Object msg);

    boolean isLastResponse(Object msg);

    boolean closeOnLastResponse();

    String findTunnelKey(Object msg) throws IOException;

    void applyStreamCodec(ChannelPipeline pipeline);

    void sendError(Channel gateway, ErrorFrame error) throws IOException;
}
