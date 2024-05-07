package com.swiftfingers.makercheckersystem.runners;

import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Slf4j
@Component
@RequiredArgsConstructor
public class RootUserRunner implements CommandLineRunner {

    private final UserRepository userRepository;


    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findAll().isEmpty()) {

            User rootUser = User.builder()
                    .username("root")
                    .email("root@system.com")
                    .password("$2a$10$pkB4D2Z0PcLwpXdgJa7Y/OfuZ3tGQ3UTaQ.drv7kzLZNbmxFGmdLW")
                    .lastName("Okafor")
                    .firstName("Obinna")
                    .loginAttempt(0)
                    .phoneNumber("08035367283")
                    .build();

            rootUser.setActive(Boolean.TRUE);

            userRepository.save(rootUser);
        }
    }
}
