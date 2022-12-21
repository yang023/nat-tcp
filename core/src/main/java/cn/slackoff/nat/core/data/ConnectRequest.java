package cn.slackoff.nat.core.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author yang
 */
@Setter
@Getter
public class ConnectRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -7983035454560236676L;

    private String clientId;

    private List<String> tunnels;
}
