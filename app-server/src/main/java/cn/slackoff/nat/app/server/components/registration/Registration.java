package cn.slackoff.nat.app.server.components.registration;

import cn.slackoff.nat.app.server.components.client.ClientInfo;
import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yang
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Registration {
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Channel> clientChannels = new HashMap<>();

    @Getter(AccessLevel.PACKAGE)
    private final Map<String, ProxyErrorConsumer> proxyErrorConsumers = new HashMap<>();

    private ClientInfo client;
    private Channel serverChannel;

    public void addClientChannel(Channel channel, ProxyErrorConsumer proxyErrorConsumer) {
        String channelId = channel.id().asLongText();
        synchronized (clientChannels) {
            clientChannels.put(channelId, channel);
        }
        channel.closeFuture().addListener(e -> clientChannels.remove(channelId));
        if (proxyErrorConsumer == null) {
            return;
        }
        synchronized (proxyErrorConsumers) {
            proxyErrorConsumers.put(channelId, proxyErrorConsumer);
        }
        channel.closeFuture().addListener(e -> proxyErrorConsumers.remove(channelId));
    }

    public Optional<Channel> findChannel(String channelId) {
        return Optional.ofNullable(clientChannels.getOrDefault(channelId, null));
    }

    public Optional<ProxyErrorConsumer> findErrorConsumer(String channelId) {
        return Optional.ofNullable(proxyErrorConsumers.getOrDefault(channelId, null));
    }
}
