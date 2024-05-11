package com.swiftfingers.makercheckersystem.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadRequestException extends AppException {

    public BadRequestException(String message){
        super(message);
    }

}
