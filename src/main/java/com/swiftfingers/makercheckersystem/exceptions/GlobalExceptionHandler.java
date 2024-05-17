package com.swiftfingers.makercheckersystem.exceptions;


import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // Handle the exception here
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> " - " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(buildResponse("Validation failed!",HttpStatus.BAD_REQUEST.value(), null, errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ModelExistsException.class)
    public ResponseEntity<?> handleModelExistsException(ModelExistsException ex, WebRequest request) {
        return new ResponseEntity<>(buildResponse(ex.getMessage(),HttpStatus.CONFLICT.value(), request, null), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundExistsException(ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(buildResponse(ex.getMessage(),HttpStatus.NOT_FOUND.value(), request, null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(BadRequestException ex, WebRequest request) {
        return new ResponseEntity<>(buildResponse(ex.getMessage(),HttpStatus.BAD_REQUEST.value(), request, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appException(AppException ex, WebRequest request) {
        return new ResponseEntity<>(buildResponse(ex.getMessage(),HttpStatus.UNPROCESSABLE_ENTITY.value(), request, null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(buildResponse(ex.getMessage(),HttpStatus.FORBIDDEN.value(), request, null), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, WebRequest request) {
        String errorMessage = ex.getParameterName() + " is missing";
        return new ResponseEntity<>(buildResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), request, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error("Error ocurred: -------- ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }

    private AppResponse buildResponse(String message, int responseCode, WebRequest request, List<String> errors) {
        return AppResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                //.path(request.getDescription(false))
                .responseCode(responseCode)
                .errors(errors)
                .build();
    }
}
