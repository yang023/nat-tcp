package cn.nat.common.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yang
 */
public final class DataSize {

    private static final Pattern PATTERN = Pattern.compile("^([+\\-]?\\d+)([a-zA-Z]{0,2})$");

    private final long size;

    public static long parseToBytes(CharSequence text) {
        return parse(text).toBytes();
    }

    public static DataSize parse(CharSequence text) {
        return parse(text, null);
    }

    public static DataSize parse(CharSequence text, DataUnit defaultUnit) {
        if (!hasLength(text)) {
            throw new IllegalArgumentException("数据表达式为空");
        }
        try {
            Matcher matcher = PATTERN.matcher(trimAllWhitespace(text));
            if (!matcher.matches()) {
                throw new IllegalArgumentException("不正确的数据表达式: '" + text + "'");
            }
            DataUnit unit = determineDataUnit(matcher.group(2), defaultUnit);
            long amount = Long.parseLong(matcher.group(1));
            return DataSize.of(amount, unit);
        } catch (Exception ex) {
            throw new IllegalArgumentException("数据表达式解析异常 '" + text + "'", ex);
        }
    }

    private DataSize(long size) {
        this.size = size;
    }

    public long toBytes() {
        return this.size;
    }

    private static DataSize of(long amount, DataUnit unit) {
        DataUnit dataUnit = Objects.requireNonNullElse(unit, DataUnit.B);
        return new DataSize(Math.multiplyExact(amount, dataUnit.size()));
    }

    private static DataUnit determineDataUnit(String suffix, DataUnit defaultUnit) {
        DataUnit defaultUnitToUse = defaultUnit == null ? DataUnit.B : defaultUnit;
        return hasLength(suffix) ? DataUnit.fromSuffix(suffix) : defaultUnitToUse;
    }

    private static CharSequence trimAllWhitespace(CharSequence text) {
        if (!hasLength(text)) {
            return text;
        }

        int len = text.length();
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static boolean hasLength(CharSequence text) {
        return (text != null && !text.isEmpty());
    }

    public enum DataUnit {

        B("B", 1),

        KB("KB", 1 << 10),

        MB("MB", 1 << 20),

        GB("GB", 1 << 30),

        TB("TB", 1L << 40);


        private final String suffix;

        private final long size;

        public static DataUnit fromSuffix(String suffix) {
            for (DataUnit candidate : values()) {
                if (candidate.suffix.equals(suffix)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("不正确的数据单位 '" + suffix + "'");
        }

        DataUnit(String suffix, long size) {
            this.suffix = suffix;
            this.size = size;
        }

        long size() {
            return this.size;
        }
    }

}