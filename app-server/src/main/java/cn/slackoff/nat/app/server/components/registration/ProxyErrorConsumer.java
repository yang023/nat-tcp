package cn.slackoff.nat.app.server.components.registration;

import cn.slackoff.nat.core.data.ErrorResponse;

/**
 * @author yang
 */
public interface ProxyErrorConsumer {

    /**
     * 代理服务返回异常，由具体的代理服务注册消费者
     */
    void accetp(ErrorResponse error) throws Exception;
}
