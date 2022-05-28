package com.humga.cloudservice.controller;

import com.humga.cloudservice.model.ErrorDTO;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice
public class FileExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        String message = e.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage())
                     .filter(Objects::nonNull).reduce(String::concat).orElse("Bad request.");

        ErrorDTO error = new ErrorDTO(message, 100);
        return handleExceptionInternal(e, error, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = { RuntimeException.class })
    public ResponseEntity<ErrorDTO> handleRT(RuntimeException e) {

        ErrorDTO error = new ErrorDTO(e.getMessage(), 101);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { DataAccessException.class })
    public ResponseEntity<ErrorDTO> handleConstraint(DataAccessException e) {
        ErrorDTO error;

        if (e.getCause() instanceof ConstraintViolationException) {
            var ex = (ConstraintViolationException) e.getCause();
            if (ex.getConstraintName().equals("uk_files")) {
                error = new ErrorDTO("Duplicate file name", 102);
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }
        error = new ErrorDTO("Database error", 101);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
