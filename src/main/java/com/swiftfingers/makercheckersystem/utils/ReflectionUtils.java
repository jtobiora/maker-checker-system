package com.swiftfingers.makercheckersystem.utils;

import com.swiftfingers.makercheckersystem.audits.annotations.ExcludeFromUpdate;
import com.swiftfingers.makercheckersystem.exceptions.AppException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
@Component
public class ReflectionUtils {
    private static final String JSON_DATA_FIELD = "jsonData";
    public <T extends BaseEntity> void pullEntityFromJson(T entity) {
        //The JSON string is stored in a field named 'jsonData'
        // Find the 'jsonData' field in the class hierarchy (from the entity to its superclasses)
        try {
            Field jsonField = findField(entity.getClass(), JSON_DATA_FIELD);
            jsonField.setAccessible(true);
            String jsonString = (String) jsonField.get(entity);
            if (jsonString != null) {
                //convert the values in 'jsonData' field to a Map. The field stores the updated resource
                Map<String, Object> updateValues = MapperUtils.fromJSON(jsonString, Map.class);
                updateEntity(entity, updateValues);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error while updating entity ", e);
            throw new AppException("Error while updating entity: " + e.getMessage());
        }
    }
    public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return findField(clazz.getSuperclass(), fieldName);
            } else {
                throw e;
            }
        }
    }

    public static <T extends BaseEntity> void updateEntity(T entity, Map<String, Object> updateValues) {
        updateValues.forEach((key, value) -> {
            try {
                Field field = findField(entity.getClass(), key);
                field.setAccessible(true);
                if (field.getAnnotation(ExcludeFromUpdate.class) == null && updateValues.containsKey(key) && value != null) {
                    updateField(entity, field, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("Error while updating entity ", e);
            }
        });
    }

    private static <T extends BaseEntity, V> void updateField(T entity, Field field, V value) throws IllegalAccessException {
        if (field.getType().isEnum()) {
            updateEnumField(entity, field, (String) value);
        } else {
            field.set(entity, value);
        }
        log.info("Field name: {} --- Updated value: {}", field.getName(), value);
    }

    private static <T extends BaseEntity> void updateEnumField(T entity, Field field, String value) throws IllegalAccessException {
        Enum<?>[] enumConstants = (Enum<?>[]) field.getType().getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            if (enumConstant.name().equals(value)) {
                field.set(entity, enumConstant);
                break;
            }
        }
    }
}
