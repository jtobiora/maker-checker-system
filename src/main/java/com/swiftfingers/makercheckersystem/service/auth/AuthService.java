package com.swiftfingers.makercheckersystem.service.auth;

import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.payload.request.ApprovalRequest;
import com.swiftfingers.makercheckersystem.payload.request.RejectionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AuthService <T> {
    Page<T> findEntityState(Class<T> entityClass, String authorizationType, Pageable pageable);

    BaseEntity approve (ApprovalRequest approvalRequest, Long entityId);

    BaseEntity reject(RejectionRequest rejectionRequest, Long entityId);
}