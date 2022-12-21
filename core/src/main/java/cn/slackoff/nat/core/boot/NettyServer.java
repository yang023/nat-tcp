package cn.slackoff.nat.core.boot;

/**
 * @author yang
 */
public interface NettyServer {

    /**
     * 通过端口启动服务
     */
    void start(int port);
}
