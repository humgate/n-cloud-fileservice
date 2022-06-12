package com.humga.cloudservice.exceptionhandlers;

import com.humga.cloudservice.model.ErrorDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;
import java.util.Objects;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlers extends ResponseEntityExceptionHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final MessageSource messageSource;
    private final Locale locale = Locale.getDefault();

    public ExceptionHandlers(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception e, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", e, 0);
        }

        if (HttpStatus.BAD_REQUEST.equals(status)) {
            ErrorDTO error = new ErrorDTO(messageSource.getMessage("bad-request", null, locale), 101);
            logger.info(e.getMessage());
            return new ResponseEntity<>(error, status);
        }

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<ErrorDTO> handleUnauthorized(BadCredentialsException e) {

        ErrorDTO error = new ErrorDTO(messageSource.getMessage("bad-credentials", null, locale), 100);
        logger.info(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);

    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException e, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ErrorDTO error = new ErrorDTO(messageSource.getMessage("bad-request", null, locale), 101);
        logger.info(e.getMessage());
        return handleExceptionInternal(e, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String message = e.getBindingResult().getAllErrors().stream().map(x -> x.getDefaultMessage())
                .filter(Objects::nonNull).reduce(String::concat).orElse(
                        messageSource.getMessage("format-invalid", null, locale));

        ErrorDTO error = new ErrorDTO(message, 102);
        logger.info(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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

        error = new ErrorDTO(messageSource.getMessage("internal.error", null, locale), 104);
        logger.info(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleRuntimeException(Exception e) {

        ErrorDTO error = new ErrorDTO(messageSource.getMessage("internal.error", null, locale), 106);
        logger.info(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
