package cn.slackoff.nat.app.server.runner;

import cn.slackoff.nat.app.server.ServerApp;
import cn.slackoff.nat.app.server.components.tunnels.TunnelGroupService;
import cn.slackoff.nat.app.server.config.ServerProps;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * @author yang
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ServerRunner implements ApplicationRunner, InitializingBean {
    private final ServerProps serverProps;

    @Setter(onMethod = @__({@Autowired(required = false)}))
    private TunnelGroupService tunnelGroupService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TunnelServer tunnelServer = new TunnelServer();
        tunnelServer.setTunnelGroupService(tunnelGroupService);
        tunnelServer.start(serverProps.getRegistration().getPort());
        HttpProxyServer httpProxyServer = new HttpProxyServer();
        httpProxyServer.setMaxRequestSize(serverProps.getHttp().getMaxRequestSize());
        httpProxyServer.setProxyHeader(serverProps.getHttp().getProxyHeader());
        httpProxyServer.setBaseDomain(serverProps.getHttp().getBaseDomain());
        httpProxyServer.start(serverProps.getHttp().getPort());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerProps.Http http = serverProps.getHttp();
        String baseDomain = http.getBaseDomain();
        Assert.hasText(baseDomain, ServerApp.CONFIG_PREFIX + ".http.base-domain is null or empty.");
    }
}
