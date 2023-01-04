package cn.slackoff.nat.app.server.config;

import cn.slackoff.nat.app.server.ServerApp;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

/**
 * @author yang
 */
@ConfigurationProperties(prefix = ServerApp.CONFIG_PREFIX)
@Setter
@Getter
public class ServerProps {

    private Registration registration = new Registration();
    private Http http = new Http();

    @Setter
    @Getter
    public static class Http {
        /**
         * 转发端口
         */
        private int port = 18080;
        /**
         * 最大报文字节数，最大支持2GB
         */
        private int maxRequestSize = (int) DataSize.ofMegabytes(10).toBytes();
        /**
         * 绑定的通道域名请求头
         */
        private String proxyHeader = "X-PROXY";

        /**
         * 根域名
         */
        private String baseDomain;
    }

    @Setter
    @Getter
    public static class Registration {
        private int port = 10243;
    }
}
