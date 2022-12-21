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
public class ConnectResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 244558663283301221L;

    private String clientId;
}
