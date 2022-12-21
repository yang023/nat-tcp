package cn.slackoff.nat.core.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author yang
 */
@Setter
@Getter
public class TunnelInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -4062480566173937499L;

    private String id;
    /**
     * 代理域名（前缀）
     */
    private String domain;

    /**
     * 内网断点，${host}:${port}
     */
    private String endpoint;

    // TODO 其他信息
    // 自定义请求头等
}
