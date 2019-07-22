package com.si.upstream.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.Nullable;

import java.io.IOException;

public class ObjectMappers {

    private static final ObjectMapper DEFAULT_INSTANCE = new ObjectMapper();

    public static class DataFormatException extends RuntimeException {
        public DataFormatException(String m) {
            super(m);
        }

        public DataFormatException(Throwable t) {
            super(t);
        }
        public DataFormatException(String m, Throwable t) {
            super(m, t);
        }
    }

    public ObjectMappers() {
    }

    public static ObjectMapper get() {
        return DEFAULT_INSTANCE;
    }

    public static <T> T mustReadValue(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        } else {
            try {
                return DEFAULT_INSTANCE.readValue(json, clazz);
            } catch (IOException e) {
                throw new DataFormatException(e);
            }
        }
    }

    public static <T> T mustReadValue(String json, TypeReference<T> typeRef) {
        if (json == null) {
            return null;
        } else {
            try {
                return DEFAULT_INSTANCE.readValue(json, typeRef);
            } catch (IOException e) {
                throw new DataFormatException(e);
            }
        }
    }

    public static String mustWriteValue(@Nullable Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return DEFAULT_INSTANCE.writeValueAsString(o);
            } catch (IOException var2) {
                throw new DataFormatException(var2);
            }
        }
    }

    public static String mustWriteValuePretty(@Nullable Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return DEFAULT_INSTANCE.writerWithDefaultPrettyPrinter().writeValueAsString(o);
            } catch (IOException var2) {
                throw new DataFormatException(var2);
            }
        }
    }

    public static JsonNode mustReadTree(@Nullable String json) {
        if (json == null) {
            return null;
        } else {
            try {
                return DEFAULT_INSTANCE.readTree(json);
            } catch (IOException var2) {
                throw new DataFormatException(var2);
            }
        }
    }

    static {
        DEFAULT_INSTANCE.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DEFAULT_INSTANCE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DEFAULT_INSTANCE.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }
}
