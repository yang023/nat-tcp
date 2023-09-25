package cn.nat.app.server.proxy;

import java.util.Optional;

/**
 * @author yang
 */
public final class ChannelSelectors {

    public static final ChannelSelector FIRST_ITEM = channels -> {
        if (channels.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(channels.iterator().next());
    };

    private static ChannelSelector defaultSelector = FIRST_ITEM;

    public static synchronized void setDefault(ChannelSelector selector) {
        ChannelSelectors.defaultSelector = selector;
    }

    public static synchronized ChannelSelector getDefault() {
        return ChannelSelectors.defaultSelector;
    }
}