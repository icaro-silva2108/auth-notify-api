package com.icaro.auth_notify.common.exceptions.globalExceptionHandler;

import com.icaro.auth_notify.common.exceptions.*;
import com.icaro.auth_notify.common.exceptions.globalExceptionHandler.model.ErrorMessageDTO;
import com.icaro.auth_notify.common.exceptions.globalExceptionHandler.model.InvalidFieldDTO;
import com.icaro.auth_notify.common.exceptions.globalExceptionHandler.model.InvalidArgumentsMessageDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalHandler {

    // GENERIC HANDLER

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> genericExceptionHandler(Exception error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                500,
                "an unexpected error has occurred"
        );
        return ResponseEntity.status(500).body(message);
    }

    // BAD REQUEST HANDLERS

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDTO> messageNotReadableHandler(HttpMessageNotReadableException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                400,
                "invalid request body"
        );
        return ResponseEntity.status(400).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InvalidArgumentsMessageDTO> methodArgumentNotValidHandler(MethodArgumentNotValidException error) {

        List<InvalidFieldDTO> errorFields = error.getFieldErrors()
                .stream()
                .map(field -> new InvalidFieldDTO(field.getField(), field.getDefaultMessage()))
                .toList();
        InvalidArgumentsMessageDTO message = new InvalidArgumentsMessageDTO(
                LocalDateTime.now(),
                400,
                "field validation error",
                errorFields
        );
        return ResponseEntity.status(400).body(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessageDTO> methodArgumentTypeMismatchHandler(MethodArgumentTypeMismatchException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                400,
                "invalid parameter type"
        );
        return ResponseEntity.status(400).body(message);
    }

    // UNAUTHORIZED HANDLERS

    @ExceptionHandler(AuthenticationError.class)
    public ResponseEntity<ErrorMessageDTO> authenticationErrorHandler(AuthenticationError error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                401,
                error.getMessage()
        );
        return ResponseEntity.status(401).body(message);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorMessageDTO> insufficientAuthenticationHandler(InsufficientAuthenticationException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                401,
                "JWT token invalid or not found"
        );
        return ResponseEntity.status(401).body(message);
    }

    // RESOURCES NOT FOUND HANDLERS

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> resourceNotFoundHandler(ResourceNotFoundException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                404,
                error.getMessage()
        );
        return ResponseEntity.status(404).body(message);
    }

    // CONFLICT HANDLERS

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessageDTO> emailAlreadyExistsHandler(EmailAlreadyExistsException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                409,
                error.getMessage()
        );
        return ResponseEntity.status(409).body(message);
    }

    // UNPROCESSABLE ENTITY HANDLERS

    @ExceptionHandler(InvalidAgeException.class)
    public ResponseEntity<ErrorMessageDTO> invalidAgeHandler(InvalidAgeException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                422,
                error.getMessage()
        );
        return ResponseEntity.status(422).body(message);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorMessageDTO> invalidPasswordHandler(InvalidPasswordException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                422,
                error.getMessage()
        );
        return ResponseEntity.status(422).body(message);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessageDTO> illegalStateHandler(IllegalStateException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                422,
                error.getMessage()
        );
        return ResponseEntity.status(422).body(message);
    }

    // FORBIDDEN HANDLERS

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageDTO> accessDeniedHandler(AccessDeniedException error) {

        ErrorMessageDTO message = new ErrorMessageDTO(
                LocalDateTime.now(),
                403,
                "forbidden access"
        );
        return ResponseEntity.status(403).body(message);
    }
}