package com.swiftfingers.makercheckersystem.controller;

import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.payload.request.ApprovalRequest;
import com.swiftfingers.makercheckersystem.payload.request.RejectionRequest;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import com.swiftfingers.makercheckersystem.repository.AuthorizationRepository;
import com.swiftfingers.makercheckersystem.service.RoleService;
import com.swiftfingers.makercheckersystem.service.auth.AuthorizationService;
import com.swiftfingers.makercheckersystem.utils.EntityTypeResolver;
import com.swiftfingers.makercheckersystem.utils.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthorizationController <T> {

    private final AuthorizationService<T> authorizationService;
    private final AuthorizationRepository authorizationRepository;
    private final RoleService roleService;

    @GetMapping("/view/entity-state")
    public ResponseEntity<AppResponse> viewEntityState(@RequestParam(name = "authType") String authType,
                                            @RequestParam(name = "entityType") String entityType,
                                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size) {


        Class<T> klass = EntityTypeResolver.resolveEntityType(entityType);
        Page<T> entityState = authorizationService.findEntityState(klass, authType, PageRequest.of(page, size, Sort.by("id").descending()));

        // Build the response
        return ResponseEntity.ok().body(Utils.buildResponse(HttpStatus.OK, "Authorized entities found", entityState));
    }

    @PostMapping("/approve/{entityId}")
    public ResponseEntity<AppResponse> approve (@RequestBody @Valid ApprovalRequest approvalRequest, @PathVariable Long entityId) {
        BaseEntity approved = authorizationService.approve(approvalRequest, entityId);
        return ResponseEntity.ok().body(Utils.buildResponse(HttpStatus.OK, "Entity approved", approved));
    }

    @PostMapping("/reject/{entityId}")
    public ResponseEntity<AppResponse> reject (@RequestBody @Valid RejectionRequest rejRequest, @PathVariable Long entityId) {
        BaseEntity rejected = authorizationService.reject(rejRequest, entityId);
        return ResponseEntity.ok().body(Utils.buildResponse(HttpStatus.OK, "Entity rejected", rejected));
    }
}
