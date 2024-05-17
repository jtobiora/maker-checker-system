package com.swiftfingers.makercheckersystem.utils;

import com.swiftfingers.makercheckersystem.exceptions.BadRequestException;
import com.swiftfingers.makercheckersystem.model.role.Role;
import com.swiftfingers.makercheckersystem.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EntityTypeResolver {
    private static final Map<String, String> classMap = new HashMap<>();

    static {
        // Define mappings here
        classMap.put("role", "com.swiftfingers.makercheckersystem.model.role.Role");
        classMap.put("user", "com.swiftfingers.makercheckersystem.model.user.User");

    }

    public static <T> Class<T> resolveEntityType(String entityName) {
        String className = classMap.get(entityName.toLowerCase());
        if (className == null) {
            throw new BadRequestException("Unknown entity: " + entityName);
        }
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Class not found for entity: {}", entityName, e);
            throw new RuntimeException("Class not found for entity: " + entityName, e);
        }
    }

    public static String getFullyQualifiedClassName (String entityName) {
        return classMap.get(entityName.toLowerCase());
    }
}
