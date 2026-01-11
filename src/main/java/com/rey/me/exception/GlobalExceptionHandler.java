package com.rey.me.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> genericException(Exception e, WebRequest request){
        log.error("Generic error");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "INTERNAL SERVER ERROR",
                request.getDescription(false),
                LocalDateTime.now());
            return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<?> usernameAlreadyExist(UsernameAlreadyExistException e, WebRequest request){
        log.error("Username already exist exception");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "USERNAME ALREADY TAKEN",
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<?> emailAlreadyExist(EmailAlreadyExistException e, WebRequest request){
        log.error("Email already exist exception");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "EMAIL ALREADY TAKEN",
                request.getDescription(false),
                LocalDateTime.now());
                return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<?> invalidEmailException(InvalidEmailException e, WebRequest request){
        log.error("Invalid email exception");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "INVALID EMAIL",
                request.getDescription(false),
                LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFoundException(UserNotFoundException e, WebRequest request){
        log.error("User not found exception");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "USER NOT FOUND",
                request.getDescription(false),
                LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<?> jobNotFoundException(JobNotFoundException e, WebRequest request){
        log.error("Job not found exception");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "JOB NOT FOUND",
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?>accessDeniedException(AccessDeniedException e, WebRequest request){
        log.error("Access Denied exception");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "YOU ARE NOT AUTHORIZED TO PERFORM THIS ACTION",
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(details, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailSendingFailedException.class)
    public ResponseEntity<?> EmailSendingFailedException(EmailSendingFailedException e, WebRequest request){
        log.error("Failed while sending email exception");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "ERROR SENDING MESSAGE",
                request.getDescription(false),
                LocalDateTime.now());
        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<?> passwordMismatchException(PasswordMismatchException e, WebRequest request){
        log.error("Password Mismatch exception");
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "PASSWORD MISMATCHED",
                request.getDescription(false),
                LocalDateTime.now());

        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation exception exception");
        log.error("Handing Invalid Request");
        String message = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage).orElse("Validation error");

        return ResponseEntity.badRequest()
                .body(message);
    }
}
