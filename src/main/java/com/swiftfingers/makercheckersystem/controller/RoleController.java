package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.payload.request.RoleRequest;
import com.swiftfingers.makercheckersystem.payload.request.validation.ValidationGroup;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.service.RoleService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<AppResponse> createRole (@Validated(ValidationGroup.CreateEntity.class) final @RequestBody RoleRequest roleRequest,
                                                   Principal principal) {
        return ResponseEntity.ok(roleService.create(roleRequest, principal.getName()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AppResponse> updateRole (@Validated(ValidationGroup.UpdateEntity.class)
                                                       final @RequestBody RoleRequest roleRequest, @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(roleService.update(roleRequest, id, principal.getName()));
    }

    @PutMapping("/toggle/{roleId}")
    public ResponseEntity<AppResponse> toggleRole (final @PathVariable Long roleId, @PathParam("toggle") boolean isActive) {
        return ResponseEntity.ok(roleService.toggleRole(roleId, isActive));
    }

}
