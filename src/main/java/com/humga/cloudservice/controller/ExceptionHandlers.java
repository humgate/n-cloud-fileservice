package com.humga.cloudservice.controller;

import com.humga.cloudservice.model.ErrorDTO;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;
import java.util.Objects;

@ControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {
    private final MessageSource messageSource;
    private final Locale locale = Locale.getDefault();

    public ExceptionHandlers(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<ErrorDTO> handleUnauthorized(BadCredentialsException e) {

        ErrorDTO error = new ErrorDTO(messageSource.getMessage("bad-credentials", null, locale), 100);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        String message = e.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage())
                .filter(Objects::nonNull).reduce(String::concat).orElse(
                        messageSource.getMessage("format-invalid", null, locale));

        ErrorDTO error = new ErrorDTO(message, 101);
        return handleExceptionInternal(e, error, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = { DataAccessException.class })
    public ResponseEntity<ErrorDTO> handleDataAccessException(DataAccessException e) {
        ErrorDTO error;

        if (e.getCause() instanceof ConstraintViolationException) {
            var ex = (ConstraintViolationException) e.getCause();
            if (ex.getConstraintName().equals("uk_files")) {
                error = new ErrorDTO(messageSource.getMessage("duplicate.object", null, locale), 103);
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        }

        error = new ErrorDTO(messageSource.getMessage("internal.error", null, locale), 102);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = { RuntimeException.class })
    public ResponseEntity<ErrorDTO> handleRuntimeException() {

        ErrorDTO error = new ErrorDTO(messageSource.getMessage("internal.error", null, locale), 102);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
