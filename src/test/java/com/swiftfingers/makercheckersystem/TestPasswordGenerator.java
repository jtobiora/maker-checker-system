package com.swiftfingers.makercheckersystem;

import com.swiftfingers.makercheckersystem.service.PasswordManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestPasswordGenerator {

    @Test
    void testGeneratePassword() {
        String password = PasswordManager.generatePassword();

        assertNotNull(password, "Password should not be null");
        assertEquals(12, password.length(), "Password length should be 12");

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (PasswordManager.SYMBOLS.indexOf(ch) >= 0) {
                hasSymbol = true;
            }
        }

        assertTrue(hasUppercase, "Password should have at least one uppercase letter");
        assertTrue(hasLowercase, "Password should have at least one lowercase letter");
        assertTrue(hasDigit, "Password should have at least one digit");
        assertTrue(hasSymbol, "Password should have at least one symbol");
    }
}
