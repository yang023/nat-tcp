package cn.nat.app.server.utils;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Random;

/**
 * @author yang
 */
public interface ChannelSelector {

    ChannelSelector RANDOM = new ChannelSelector() {
        private final Random random = new Random();

        @Override
        public Channel select(Collection<Channel> channels) {
            int i = random.nextInt(0, channels.size());
            if (i == 0) {
                return channels.iterator().next();
            }
            int i1 = 0;
            for (Channel channel : channels) {
                i1++;
                if (i1 == i) {
                    return channel;
                }
            }
            return null;
        }
    };

    Channel select(Collection<Channel> channels);
}
