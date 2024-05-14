package com.swiftfingers.makercheckersystem;

import com.swiftfingers.makercheckersystem.model.user.Token;
import com.swiftfingers.makercheckersystem.service.redis.LoginTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.Instant;

import static com.swiftfingers.makercheckersystem.utils.ValidationUtils.isValidPassword;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MakerCheckerSystemApplicationTests {
	@Autowired
	LoginTokenService tokenService;
	@Test
	void testValidPasswords() {
		assertTrue(isValidPassword("Passw0rd!")); // Minimum length, contains digits, uppercase, lowercase, and symbols
		assertTrue(isValidPassword("MyPa$$w0rd")); // Minimum length, contains digits, uppercase, lowercase, and symbols
		assertFalse(isValidPassword("StrongP@ss")); // Minimum length, contains digits, uppercase, lowercase, and symbols
	}

	@Test
	void testInvalidPasswords() {
		assertFalse(isValidPassword("short")); // Too short
		assertFalse(isValidPassword("nocapsdigitsorsymbols")); // Doesn't contain uppercase, lowercase, digits, or symbols
		assertFalse(isValidPassword("missingSymbol1")); // Doesn't contain symbols
		assertFalse(isValidPassword("MISSINGLOWER@DIGIT")); // Doesn't contain lowercase letters
	}

//	@Test
//	void testTokenGeneration() {
//		// Create a token service
//
//		// Generate tokens for different unique identifiers
//		String token1 = tokenService.generate2FAToken("user123");
//		String token2 = tokenService.generate2FAToken("session456");
//
//		// Ensure tokens are not null
//		assertNotNull(token1);
//		assertNotNull(token2);
//
//		// Ensure tokens have correct length
//		assertEquals(4, token1.length());
//		assertEquals(4, token2.length());
//
//		// Ensure tokens are numeric
//		assertTrue(token1.matches("\\d+"));
//		assertTrue(token2.matches("\\d+"));
//	}
//
//	@Test
//	void testTokenExpiration() {
//		// Create a token service
//
//		// Generate a token
//		String token = tokenService.generate2FAToken("user123");
//
//		// Create a token object with a creation time 31 seconds ago
//		Token expiredToken = new Token(token, Instant.now().minusSeconds(31));
//
//		// Ensure token is not valid
//		assertFalse(tokenService.is2FATokenValid(expiredToken));
//
//		// Create a token object with a creation time 29 seconds ago
//		Token validToken = new Token(token, Instant.now().minusSeconds(29));
//
//		// Ensure token is valid
//		assertTrue(tokenService.is2FATokenValid(validToken));
//	}
}
