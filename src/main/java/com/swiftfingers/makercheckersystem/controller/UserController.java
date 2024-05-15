package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    @GetMapping
    @Secured("ROLE_CREATE_USER")
    public String findAllUsers (){
        return "Users found";
    }
}
