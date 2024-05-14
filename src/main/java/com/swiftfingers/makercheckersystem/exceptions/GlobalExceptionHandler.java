package com.swiftfingers.makercheckersystem.exceptions;


import com.amazonaws.services.apigateway.model.UnauthorizedException;
import com.swiftfingers.makercheckersystem.payload.response.AppResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//
//        List<String> errors =  ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(error -> " - " +  error.getDefaultMessage())
//                .collect(Collectors.toList());
//
//        return new ResponseEntity<>(buildResponse("Validation failed!",HttpStatus.BAD_REQUEST.value(), request, errors), HttpStatus.BAD_REQUEST);
//
//    }

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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(buildResponse(ex.getMessage(),HttpStatus.FORBIDDEN.value(), request, null), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unAuthorizedException(UnauthorizedException ex, WebRequest request) {
        return new ResponseEntity<>(buildResponse(ex.getMessage(),HttpStatus.UNAUTHORIZED.value(), request, null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        // Handle the exception here
        // You can log the exception or return a custom error message
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
