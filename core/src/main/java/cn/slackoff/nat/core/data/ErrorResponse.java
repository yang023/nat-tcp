package cn.slackoff.nat.core.data;

import cn.slackoff.nat.core.protocol.Command;
import cn.slackoff.nat.core.protocol.ContentType;
import cn.slackoff.nat.core.protocol.FrameOutput;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author yang
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4658746706916814720L;

    /**
     * 错误操作
     */
    private final Command from;

    /**
     * 错误提示
     */
    private final String message;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static ErrorResponse create(@JsonProperty("command") Command command,
                                       @JsonProperty("message") String message) {
        return new ErrorResponse(command, message);
    }

    public static ErrorResponse create(Command command) {
        return new ErrorResponse(command, "Unknown error.");
    }

    public void write(FrameOutput output) {
        output.command(Command.ERROR_RESPONSE)
              .contentType(ContentType.JSON)
              .write(this);
    }
}
