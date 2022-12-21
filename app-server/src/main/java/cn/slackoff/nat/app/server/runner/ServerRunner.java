package cn.slackoff.nat.app.server.runner;

import cn.slackoff.nat.app.server.components.client.ClientInfo;
import cn.slackoff.nat.app.server.components.client.ClientRepository;
import cn.slackoff.nat.core.data.TunnelInfo;
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
@Component
public class ServerRunner implements ApplicationRunner, InitializingBean {

    @Setter(onMethod = @__({@Autowired(required = false)}))
    private ClientRepository clientRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TunnelServer tunnelServer = new TunnelServer();
        tunnelServer.setClientRepository(clientRepository);
        tunnelServer.start(10243);
        new HttpProxyServer().start(18080);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setId("client1");
        List<TunnelInfo> list = new ArrayList<>();
        TunnelInfo tunnel = new TunnelInfo();
        tunnel.setId("tunnel1");
        tunnel.setDomain("dev.proxy.yang023.cn");
        tunnel.setEndpoint("127.0.0.1:8080");
        list.add(tunnel);
        tunnel = new TunnelInfo();
        tunnel.setId("tunnel2");
        tunnel.setDomain("test.proxy.yang023.cn");
        tunnel.setEndpoint("127.0.0.1:9090");
        list.add(tunnel);
        clientInfo.setTunnels(list);
        this.clientRepository.saveClient(clientInfo);
    }
}
