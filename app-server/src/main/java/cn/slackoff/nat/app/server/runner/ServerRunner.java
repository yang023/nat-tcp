package cn.slackoff.nat.app.server.runner;

import cn.slackoff.nat.app.server.components.client.ClientInfo;
import cn.slackoff.nat.app.server.components.client.ClientRepository;
import cn.slackoff.nat.app.server.config.ServerProps;
import cn.slackoff.nat.core.data.TunnelInfo;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yang
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ServerRunner implements ApplicationRunner, InitializingBean {
    private final ServerProps serverProps;

    @Setter(onMethod = @__({@Autowired(required = false)}))
    private ClientRepository clientRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TunnelServer tunnelServer = new TunnelServer();
        tunnelServer.setClientRepository(clientRepository);
        tunnelServer.start(serverProps.getRegistration().getPort());
        HttpProxyServer httpProxyServer = new HttpProxyServer();
        httpProxyServer.setMaxRequestSize(serverProps.getHttp().getMaxRequestSize());
        httpProxyServer.setProxyHeader(serverProps.getHttp().getProxyHeader());
        httpProxyServer.start(serverProps.getHttp().getPort());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setId("client1");
        List<TunnelInfo> list = new ArrayList<>();
        TunnelInfo tunnel = new TunnelInfo();
        tunnel.setId("tunnel1");
        tunnel.setDomain("dev.nat.yang023.cn");
        tunnel.setEndpoint("127.0.0.1:8080");
        list.add(tunnel);
        tunnel = new TunnelInfo();
        tunnel.setId("tunnel2");
        tunnel.setDomain("test.nat.yang023.cn");
        tunnel.setEndpoint("127.0.0.1:9090");
        list.add(tunnel);
        clientInfo.setTunnels(list);
        this.clientRepository.saveClient(clientInfo);
    }
}
