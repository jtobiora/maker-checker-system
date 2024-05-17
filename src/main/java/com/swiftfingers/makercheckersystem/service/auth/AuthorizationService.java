package com.swiftfingers.makercheckersystem.service.auth;

import com.swiftfingers.makercheckersystem.model.BaseEntity;
import com.swiftfingers.makercheckersystem.payload.request.AuthRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorizationService<T> {
   Page<T> findEntityState(Class<T> entityClass, String authorizationType, Pageable pageable);

   BaseEntity approve (AuthRequest authRequest, Long entityId);
}
