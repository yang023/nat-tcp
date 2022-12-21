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
public class PrepareRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5934840832860926330L;

    private TunnelInfo tunnel;

    private String channelId;
}
