package cn.slackoff.nat.core.protocol;

import javax.annotation.Nonnull;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author yang
 */
public class Headers implements Serializable {
    @Serial
    private static final long serialVersionUID = -4998494074819803405L;

    private final Map<String, String> values = new HashMap<>();

    Headers() {
    }

    public void set(String key, String value) {
        this.values.put(key, value);
    }

    @Nonnull
    public String get(String key) {
        return this.values.getOrDefault(key, "");
    }

    public Stream<Map.Entry<String, String>> stream() {
        return this.values.entrySet().stream();
    }
}
