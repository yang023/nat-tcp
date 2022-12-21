package cn.slackoff.nat.app.server.components.client;

import cn.slackoff.nat.core.data.TunnelInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author yang
 */
@Getter
@Setter
public class ClientInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -4881373295264245086L;
    private String id;
    private List<TunnelInfo> tunnels;
}
