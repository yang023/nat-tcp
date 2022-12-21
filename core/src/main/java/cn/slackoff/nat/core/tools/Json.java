package cn.slackoff.nat.core.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author yang
 */
public final class Json {
    private static final ObjectMapper mapper = new ObjectMapper();

    private Json() {
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to write object to json.", e);
        }
    }

    public static <T> T fromJson(byte[] json, Class<T> type) {
        return readJsonInternal(() -> mapper.readValue(json, type));
    }

    public static <T> T fromJson(byte[] json, TypeReference<T> type) {
        return readJsonInternal(() -> mapper.readValue(json, type));
    }

    private static <T> T readJsonInternal(JsonProcessor<T> processor) {
        try {
            return processor.exec();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read object from json.", e);
        }
    }

    interface JsonProcessor<T> {
        T exec() throws IOException;
    }
}
