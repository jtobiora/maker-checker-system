package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.enums.ModelState;
import com.swiftfingers.makercheckersystem.payload.request.RoleRequest;
import com.swiftfingers.makercheckersystem.payload.request.validation.ValidationGroup;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.service.RoleService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<AppResponse> createRole (@Validated(ValidationGroup.CreateRole.class) final @RequestBody RoleRequest roleRequest) {
        return ResponseEntity.ok(roleService.create(roleRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AppResponse> updateRole (@Validated(ValidationGroup.UpdateRole.class)
                                                       final @RequestBody RoleRequest roleRequest, @PathVariable Long id) {
        return ResponseEntity.ok(roleService.update(roleRequest, id));
    }

}
