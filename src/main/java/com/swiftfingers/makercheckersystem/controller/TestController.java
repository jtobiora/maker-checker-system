package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.audits.annotations.CreateOperation;
import com.swiftfingers.makercheckersystem.audits.annotations.UpdateOperation;
import com.swiftfingers.makercheckersystem.payload.request.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class TestController {
    private final TestRepository testRepository;

    @PostMapping("/test")
    @Secured("ROLE_CREATE_USER")
    @CreateOperation
//    @PreAuthorize("hasAnyRole('ROLE_CREATE_USER', 'ROLE_CREATE_ROLE')")
//    @PreAuthorize("hasAnyAuthority('ROLE_ASSIGN_ROLE', 'ROLE_UPDATE_USER')")
//    @PreAuthorize("hasAuthority('ROLE_DELETE_USER')")
//    @PreAuthorize("hasRole('ROLE_CREATE_ROLE')")
    public ResponseEntity<TestUser> test (@RequestBody SignUpRequest signUpRequest) {

        TestUser testUser = new TestUser();
        testUser.setEmail(signUpRequest.getEmail());
        testUser.setName(signUpRequest.getFirstName());
        testUser.setActive(false);
        return ResponseEntity.ok(testRepository.save(testUser));
    }

    @PutMapping("/test/{id}")
    @Secured("ROLE_EDIT_USER")
    @UpdateOperation
    public ResponseEntity<TestUser> updateTest (@RequestBody SignUpRequest req,@PathVariable Long id) {

        TestUser testUser = testRepository.findById(id).orElseThrow(null);
        testUser.setName(req.getFirstName());
        testUser.setEmail(req.getEmail());
        testUser.setActive(false);
        return ResponseEntity.ok(testRepository.save(testUser));
    }



    @DeleteMapping("/test/{id}")
    @Secured("ROLE_DELETE_USER")
    public String testing (@PathVariable Long id) {
        TestUser testUser = testRepository.findById(id).orElseThrow(null);
        testRepository.delete(testUser);
        return "Deleted";
    }
}
