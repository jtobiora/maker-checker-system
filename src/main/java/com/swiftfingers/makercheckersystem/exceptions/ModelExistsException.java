package com.swiftfingers.makercheckersystem.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ModelExistsException extends AppException{
    private String message;
    private Object[] params;

    public ModelExistsException() {

    }

    public ModelExistsException(String message) {
        super(message);
        this.message = message;
    }

    public ModelExistsException(String message, Object...params) {
        super(message);
        this.params = params;
        this.message = message;
    }


}

