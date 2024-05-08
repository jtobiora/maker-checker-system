package com.swiftfingers.makercheckersystem.exceptions;

import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private List<String> errors;

    public ErrorDetails(Date timestamp, String message, String error) {
        this.timestamp = timestamp;
        this.message = message;
        this.errors = Arrays.asList(error);
    }

    public ErrorDetails(String error) {
        this.timestamp = new Date();
        this.message = "Unable to process request. Please try again.";
        this.errors = Arrays.asList(error);
    }


}
