package cn.nat.app.server.utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yang
 */
public abstract class ChannelMapping {
    private final ConcurrentMap<String, ChannelGroup> channels = new ConcurrentHashMap<>();

    protected ChannelMapping() {
    }

    protected ChannelGroup findChannels(String tunnel) {
        return channels.computeIfAbsent(tunnel, __ -> new DefaultChannelGroup(GlobalEventExecutor.INSTANCE));
    }

    protected void addChannel(String tunnel, Channel channel) {
        findChannels(tunnel).add(channel);
        channel.closeFuture().addListener(f -> findChannels(tunnel).remove(channel));
    }

    protected Channel selectChannel(String tunnel, ChannelSelector selector) {
        ChannelGroup group = findChannels(tunnel);
        if (group.isEmpty()) {
            return null;
        }

        return selector.select(group);
    }
}
