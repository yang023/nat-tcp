package cn.nat.app.server.proxy;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Optional;

/**
 * @author yang
 */
public interface ChannelSelector {

    Optional<Channel> select(Collection<Channel> channels);
}