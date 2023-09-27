package cn.nat.app.server.command;

import cn.nat.app.server.utils.ChannelMapping;
import cn.nat.app.server.utils.ChannelSelector;
import cn.nat.common.protocol.Frame;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import javax.annotation.Nullable;

/**
 * @author yang
 */
public final class CommandChannelMapping extends ChannelMapping {
    private CommandChannelMapping() {
    }

    public static void add(String tunnel, Channel channel) {
        getInstance().addChannel(tunnel, channel);
    }

    @Nullable
    public static ChannelFuture sendFrameTo(String tunnel, Frame frame) {
        Channel channel = getInstance().selectChannel(tunnel, ChannelSelector.RANDOM);
        if (channel == null) {
            return null;
        }
        return channel.writeAndFlush(frame.serialize());
    }

    private static class InstanceHolder {
        private static final CommandChannelMapping INSTANCE = new CommandChannelMapping();
    }

    private static CommandChannelMapping getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
