package cn.slackoff.nat.app.client.runner;

import cn.slackoff.nat.app.client.components.context.ClientContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yang
 */
@Slf4j
@Component
public class ClientRunner implements ApplicationRunner {
    // TODO
    private final String host = "127.0.0.1";
    private final int port = 10243;
    private final int retry = 3;
    private final String clientId = "client1";
    private final List<String> tunnels = List.of("tunnel1", "tunnel2");

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClientContextHolder.initialize(this.clientId, this.tunnels);

        TunnelClient client = new TunnelClient();
        client.setRetryCount(retry);
        client.onClosed(e -> System.exit(0));
        client.connect(host, port);
    }
}
