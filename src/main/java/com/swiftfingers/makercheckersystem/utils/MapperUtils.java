package com.swiftfingers.makercheckersystem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.swiftfingers.makercheckersystem.audits.annotations.Sensitive;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class MapperUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeObjectExcludingSensitiveFields(Object object) {
        try {
            // Get all fields of the object
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                // Check if the field is annotated with @Sensitive
                if (field.isAnnotationPresent(Sensitive.class)) {
                    // Set the field as accessible
                    field.setAccessible(true);
                    // Exclude the field from serialization
                    field.set(object, null);
                }
            }
            // Serialize the object excluding sensitive fields
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Serialization of object failed ", e);
            return null;
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON", e);
        }
    }

    public static <T> T fromJSON(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON", e);
        }
    }

    public static String toJSON(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }

    public static <T> List<T> readListValue(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing JSON list", e);
        }
    }
}
