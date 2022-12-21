package cn.slackoff.nat.core.protocol;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author yang
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Command {
    // *********************************************************************
    // 错误响应
    // *********************************************************************
    ERROR_RESPONSE((byte) 0x00),

    // *********************************************************************
    // 连接注册
    // *********************************************************************
    CONNECT_REQUEST((byte) 0x01),
    CONNECT_RESPONSE((byte) 0x11),

    // *********************************************************************
    // 代理通道准备
    // *********************************************************************
    PREPARE_REQUEST((byte) 0x02),
    PREPARE_RESPONSE((byte) 0x12),

    // *********************************************************************
    // 报文转发
    // *********************************************************************
    STREAM_REQUEST((byte) 0x03),
    STREAM_RESPONSE((byte) 0x13),
    ;

    @Getter
    private final byte code;

    public static Optional<Command> ofCode(byte code) {
        return Stream.of(Command.values()).filter(it -> it.getCode() == code).findFirst();
    }
}
