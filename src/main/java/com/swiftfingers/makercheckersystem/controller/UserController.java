package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.model.user.User;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.UserRepository;
import com.swiftfingers.makercheckersystem.service.UserService;
import com.swiftfingers.makercheckersystem.utils.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping
    public ResponseEntity<AppResponse> findAllUsers (@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size){
        return ResponseEntity.ok(userService.findAllUsers(PageRequest.of(page, size, Sort.by("id").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppResponse> findById (@PathVariable Long id) {
        AppResponse response = Utils.buildResponse(HttpStatus.OK, "User found", userService.findById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping ("/create")
    public ResponseEntity<AppResponse> createUser (@Valid final @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(Utils.buildResponse(HttpStatus.CREATED,
                "User account was successfully created. Check your mail for your password.", userService.createUser(signUpRequest)));
    }

    @PostMapping ("/update/{userId}")
    public ResponseEntity<AppResponse> updateUser (@Valid final @RequestBody SignUpRequest signUpRequest, @PathVariable Long userId) {
        User user = userService.updateUser(signUpRequest, userId);
        return ResponseEntity.ok(Utils.buildResponse(HttpStatus.CREATED,
                "Updated user request has been sent for Authorizer's action", user));
    }
}
