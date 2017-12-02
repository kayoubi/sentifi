package com.sentifi.stock.controller;

import com.sentifi.stock.exceptions.QuandlException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @author khaled
 */
@ControllerAdvice
public class GlobalController {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity handleConversionError(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>("You provided " + ex.getValue()+ " for " + ex.getName() + ". This is not a recognized date format. Please provide yyyy-MM-dd", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(QuandlException.class)
    public ResponseEntity handleQuandlError(QuandlException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleError(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
