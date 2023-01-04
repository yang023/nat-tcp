package cn.slackoff.nat.app.server.components.registration;

import cn.slackoff.nat.app.server.components.tunnels.TunnelGroup;
import cn.slackoff.nat.core.data.TunnelInfo;
import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationManager {
    private static final ConcurrentMap<String, TunnelInfo> domainMapping
            = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, Registration> domainRegistration
            = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, Registration> clientIdRegistration
            = new ConcurrentHashMap<>();

    public static void create(TunnelGroup tunnelGroup, Channel channel) {
        Registration registration = new Registration();
        registration.setTunnelGroup(tunnelGroup);
        registration.setServerChannel(channel);

        List<TunnelInfo> tunnels = tunnelGroup.getTunnels();
        for (TunnelInfo tunnel : tunnels) {
            domainMapping.put(tunnel.getDomain(), tunnel);
            domainRegistration.put(tunnel.getDomain(), registration);
            channel.closeFuture().addListener(e -> domainMapping.remove(tunnel.getDomain()));
            channel.closeFuture().addListener(e -> domainRegistration.remove(tunnel.getDomain()));
        }

        clientIdRegistration.put(tunnelGroup.getClientId(), registration);
        channel.closeFuture().addListener(e -> clientIdRegistration.remove(tunnelGroup.getClientId()));
    }

    public static Optional<TunnelInfo> findTunnelByDomain(String domain) {
        return Optional.ofNullable(domainMapping.getOrDefault(domain, null));
    }

    public static Optional<Registration> findRegistrationByDomain(String domain) {
        return Optional.ofNullable(domainRegistration.getOrDefault(domain, null));
    }

    public static Optional<Registration> findRegistrationByClientId(String clientId) {
        return Optional.ofNullable(clientIdRegistration.getOrDefault(clientId, null));
    }
}
