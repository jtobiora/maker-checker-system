package com.swiftfingers.makercheckersystem.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppException extends RuntimeException {
    public static final String ERR_COMP_EXISTS = "core.company.exists";

    protected String message;
    protected Object[] params;

    public AppException(String message) {
        super(message);
        this.message = message;
    }
    public AppException(Exception e) {
        super(e);
    }
}