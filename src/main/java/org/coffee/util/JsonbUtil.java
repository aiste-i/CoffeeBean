package org.coffee.util;

import lombok.Getter;

import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.lang.reflect.Type;

@Stateless
public class JsonbUtil {
    @Getter
    private static final Jsonb jsonb;

    static {
        JsonbConfig config = new JsonbConfig()
                .withNullValues(true)
                .withFormatting(false);
        jsonb = JsonbBuilder.create(config);
    }

    public static <T> String toJson(T object) {
        if (object == null) return null;
        return jsonb.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        if (json == null || json.trim().isEmpty()) return null;
        return jsonb.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        if (json == null || json.trim().isEmpty()) return null;
        return jsonb.fromJson(json, type);
    }
}
