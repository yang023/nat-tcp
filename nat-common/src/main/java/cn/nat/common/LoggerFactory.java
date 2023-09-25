package cn.nat.common;

import org.slf4j.Logger;

/**
 * @author yang
 */
public final class LoggerFactory {
    private static final String NAT_LOG_PREFIX = "nat.logger";

    public static Logger getLogger(String module) {
        return org.slf4j.LoggerFactory.getLogger("%s.%s".formatted(NAT_LOG_PREFIX, module));
    }
}