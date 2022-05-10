package com.humga.cloudservice.controller;

import com.humga.cloudservice.exceptions.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CloudExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { BadRequestException.class })
    protected ResponseEntity<Object> handleConflict(BadRequestException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(),new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
