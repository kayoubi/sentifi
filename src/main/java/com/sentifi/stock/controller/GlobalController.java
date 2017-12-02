package com.sentifi.stock.controller;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author khaled
 */
@ControllerAdvice
public class GlobalController {

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity handleError(ConversionFailedException ex) {
        return new ResponseEntity<>("You provided " + ex.getValue() + " for date param. This is not a recognized date format. Please provide yyyy-MM-dd", HttpStatus.BAD_REQUEST);
    }
}
