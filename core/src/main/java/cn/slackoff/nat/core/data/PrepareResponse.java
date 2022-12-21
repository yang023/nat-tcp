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
public class PrepareResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -8448156821994325031L;

    private String channelId;

    private String clientId;

    private String endpoint;
}
