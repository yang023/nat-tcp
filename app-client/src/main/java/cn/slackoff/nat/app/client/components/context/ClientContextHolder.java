package cn.slackoff.nat.app.client.components.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author yang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientContextHolder {
    private static String clientId;
    private static List<String> enableTunnels;

    private static ClientContext context;

    public static synchronized void initialize(String clientId, List<String> enableTunnels) {
        if (context == null) {
            ClientContextHolder.clientId = clientId;
            ClientContextHolder.enableTunnels = Collections.unmodifiableList(enableTunnels);
        }
    }

    public static synchronized ClientContext getContext() {
        synchronized (ClientContextHolder.class) {
            if (context == null) {
                context = new ClientContext(clientId, enableTunnels);
            }
        }
        return context;
    }
}
