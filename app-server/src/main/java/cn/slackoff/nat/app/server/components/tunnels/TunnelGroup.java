package cn.slackoff.nat.app.server.components.tunnels;

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
public class TunnelGroup implements Serializable {
    @Serial
    private static final long serialVersionUID = -4881373295264245086L;
    private String clientId;
    private List<TunnelInfo> tunnels;
}
