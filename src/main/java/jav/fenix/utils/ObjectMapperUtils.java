package jav.fenix.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance;

/**
 * @author w.vela
 */
public final class ObjectMapperUtils {

    private static final String EMPTY_JSON = "{}";

    private static final String EMPTY_ARRAY_JSON = "[]";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.disable(FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static String toJSON(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出格式化好的json
     * 请不要在输出log时使用
     * <p>
     * 一般只用于写结构化数据到ZooKeeper时使用（为了更好的可读性）
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void toJSON(Object obj, OutputStream writer) {
        if (obj == null) {
            return;
        }
        try {
            MAPPER.writeValue(writer, obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJSON(byte[] bytes, Class<T> valueType) {
        if (bytes == null) {
            return null;
        }
        try {
            return MAPPER.readValue(bytes, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> valueType) {
        if (json == null) {
            return null;
        }
        if (valueType == String.class) {
            return (T) json;
        }
        try {
            return MAPPER.readValue(json, valueType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static <T> T fromNullableJSON(Object value, Class<T> valueType) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return fromJSON((String) value, valueType);
        } else {
            return null;
        }
    }

    public static <T> T value(Object rawValue, Class<T> type) {
        return MAPPER.convertValue(rawValue, type);
    }

    public static <T> T update(T rawValue, String newProperty) {
        try {
            return MAPPER.readerForUpdating(rawValue).readValue(newProperty);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T value(Object rawValue, TypeReference<T> type) {
        return MAPPER.convertValue(rawValue, type);
    }

    public static <T> T value(Object rawValue, JavaType type) {
        return MAPPER.convertValue(rawValue, type);
    }

    public static <T> T unwrapJsonP(String raw, Class<T> type) {
        return fromJSON(unwrapJsonP(raw), type);
    }

    private static String unwrapJsonP(String raw) {
        raw = StringUtils.trim(raw);
        raw = StringUtils.removeEnd(raw, ";");
        raw = raw.substring(raw.indexOf('(') + 1);
        raw = raw.substring(0, raw.lastIndexOf(')'));
        raw = StringUtils.trim(raw);
        return raw;
    }

    public static <E, T extends Collection<E>> T fromJSON(String json, Class<? extends Collection> collectionType, Class<E> valueType) {
        if (StringUtils.isEmpty(json)) {
            json = EMPTY_ARRAY_JSON;
        }
        try {
            return MAPPER.readValue(json, defaultInstance().constructCollectionType(collectionType, valueType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * use {@link #fromJson(String)} instead
     */
    public static <K, V, T extends Map<K, V>> T fromJSON(String json, Class<? extends Map> mapType, Class<K> keyType, Class<V> valueType) {
        if (StringUtils.isEmpty(json)) {
            json = EMPTY_JSON;
        }
        try {
            return MAPPER.readValue(json, defaultInstance().constructMapType(mapType, keyType, valueType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJSON(InputStream inputStream, Class<T> type) {
        try {
            return MAPPER.readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <E, T extends Collection<E>> T fromJSON(byte[] bytes, Class<? extends Collection> collectionType, Class<E> valueType) {
        try {
            return MAPPER.readValue(bytes, defaultInstance().constructCollectionType(collectionType, valueType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <E, T extends Collection<E>> T fromJSON(InputStream inputStream, Class<? extends Collection> collectionType, Class<E> valueType) {
        try {
            return MAPPER.readValue(inputStream, defaultInstance().constructCollectionType(collectionType, valueType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> fromJson(InputStream is) {
        return fromJSON(is, Map.class, String.class, Object.class);
    }

    public static Map<String, Object> fromJson(String string) {
        return fromJSON(string, Map.class, String.class, Object.class);
    }

    public static Map<String, String> from(String string) {
        return fromJSON(string, Map.class, String.class, String.class);
    }

    public static Map<String, Object> fromJson(byte[] bytes) {
        return fromJSON(bytes, Map.class, String.class, Object.class);
    }

    /**
     * use {@link #fromJson(byte[])} instead
     */
    public static <K, V, T extends Map<K, V>> T fromJSON(byte[] bytes, Class<? extends Map> mapType, Class<K> keyType, Class<V> valueType) {
        try {
            return MAPPER.readValue(bytes, defaultInstance().constructMapType(mapType, keyType, valueType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * use {@link #fromJson(InputStream)} instead
     */
    public static <K, V, T extends Map<K, V>> T fromJSON(InputStream inputStream, Class<? extends Map> mapType, Class<K> keyType,
                                                         Class<V> valueType) {
        try {
            return MAPPER.readValue(inputStream, defaultInstance().constructMapType(mapType, keyType, valueType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @return the MAPPER
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }

}
