package com.swiftfingers.makercheckersystem.utils;

import com.swiftfingers.makercheckersystem.audits.annotations.ExcludeFromUpdate;
import com.swiftfingers.makercheckersystem.exceptions.AppException;
import com.swiftfingers.makercheckersystem.model.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ReflectionUtils {

    @Value("${app.key}")
    private String key;
    private static final String JSON_DATA_FIELD = "jsonData";

    /*
     *  Pulls out the entity from the JSON string stored in 'jsonData' field and then uses it to update the entity passed
     *  in the argument
     * */
    public <T extends BaseEntity> Map<String, Object> pullEntityFromJson(T entity) {
        //The JSON string is stored in a field named 'jsonData'
        // Find the 'jsonData' field in the class hierarchy (from the entity to its superclasses)
        try {
            Field jsonField = findField(entity.getClass(), JSON_DATA_FIELD);
            jsonField.setAccessible(true);
            String jsonString = (String) jsonField.get(entity);
            if (jsonString != null) {
                //decrypt the encrypted string in 'jsonData' column
                String decryptedEntity = EncryptionUtil.decrypt(jsonString, key);
                log.info("Decrypted entity ....{}", decryptedEntity);
                //convert the values in 'jsonData' field to a Map. The field stores the updated resource
                return MapperUtils.fromJSON(decryptedEntity, Map.class);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error while updating entity ", e);
            throw new AppException("Error while updating entity: " + e.getMessage());
        }
        return new HashMap<>();
    }

    /*
     * Finds any field in a class or its superclasses
     * */
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

    /*
     * Updates the entity using the values pulled out from the JSON string and which is stored in a Map
     * @entity - the resource being updated
     * @updateValues - the values to be updated with
     * */
    public static <T extends BaseEntity> void updateEntity(T entity, Map<String, Object> updateValues) {
        updateValues.forEach((key, value) -> {
            try {
                Field field = findField(entity.getClass(), key);
                field.setAccessible(true);
                if (field.getAnnotation(ExcludeFromUpdate.class) == null && updateValues.containsKey(key)) {
                    updateField(entity, field, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("Error while updating entity ", e);
            }
        });
    }

    /*
     * Updates the entity passed in as argument.
     * @entity - The entity to update
     * @field - The field of the entity to update
     * @value - The value to be updated
     * */
    private static <T extends BaseEntity, V> void updateField (T entity, Field field, V value) throws IllegalAccessException {
        if (field.getType().isEnum()) {
            updateEnumField(entity, field, (String) value);
        } else {
            field.set(entity, value);
        }
        log.debug("Field name: {} --- Updated value: {}", field.getName(), value);
    }

    /*
     * Updates an enum field
     * */
    private static <T extends BaseEntity> void updateEnumField (T entity, Field field, String value) throws IllegalAccessException {
        Enum<?>[] enumConstants = (Enum<?>[]) field.getType().getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            if (enumConstant.name().equals(value)) {
                field.set(entity, enumConstant);
                break;
            }
        }
    }

    public static <T extends BaseEntity> void retainNecessaryFields(T source, T target) {
        try {
            // Copy the ID
            Field idField = findField(source.getClass(), "id");
            idField.setAccessible(true);
            idField.set(target, idField.get(source));

            // Add any other necessary fields to be retained from the source entity
            // Example: Copy createdAt, createdBy, etc.
            // Field createdAtField = findField(source.getClass(), "createdAt");
            // createdAtField.setAccessible(true);
            // createdAtField.set(target, createdAtField.get(source));
            //
            // Field createdByField = findField(source.getClass(), "createdBy");
            // createdByField.setAccessible(true);
            // createdByField.set(target, createdByField.get(source));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error retaining necessary fields from source to target", e);
        }
    }
}
