package cn.slackoff.nat.app.server.domains.tunnels;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yang
 */
@Setter
@Getter
@Table("tunnel_info")
public class TunnelEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1818783587657608492L;

    @Id
    @Column("tunnel_id")
    private String id;

    /**
     * 通道名称
     */
    @Column("tunnel_name")
    private String tunnelName;

    /**
     * 代理域名
     */
    @Column("proxy_domain")
    private String proxyDomain;

    /**
     * 代理内网端点
     */
    @Column("proxy_endpoint")
    private String proxyEndpoint;

    /**
     * 所属分组的客户端id
     */
    @Column("client_id")
    private String clientId;

    /**
     * 创建时间
     */
    @Column("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column("update_time")
    private LocalDateTime updateTime;
}
