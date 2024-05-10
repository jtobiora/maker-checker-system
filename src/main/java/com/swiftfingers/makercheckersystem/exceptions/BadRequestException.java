package com.swiftfingers.makercheckersystem.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
@Slf4j
public class BadRequestException extends AppException {

    public BadRequestException(String message){
        super(message);
    }

}
