package com.swiftfingers.makercheckersystem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftfingers.makercheckersystem.audits.annotations.Sensitive;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class MapperUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
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
}
