package com.REY.Me.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> genericException(Exception e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "INTERNAL SERVER ERROR",
                request.getDescription(false),
                LocalDateTime.now()

        );
            return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<?> usernameAlreadyExist(UsernameAlreadyExistException e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "USERNAME ALREADY TAKEN",
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<?> emailAlreadyExist(EmailAlreadyExistException e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "EMAIL ALREADY TAKEN",
                request.getDescription(false),
                LocalDateTime.now()
        );
                return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<?> invalidEmailException(InvalidEmailException e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "INVALID EMAIL",
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFoundException(UserNotFoundException e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "USER NOT FOUND",
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<?> jobNotFoundException(JobNotFoundException e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "JOB NOT FOUND",
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?>accessDeniedException(AccessDeniedException e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "YOU ARE NOT AUTHORIZED TO PERFORM THIS ACTION",
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(details, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailSendingFailedException.class)
    public ResponseEntity<?> EmailSendingFailedException(EmailSendingFailedException e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "ERROR SENDING MESSAGE",
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<?> passwordMismatchException(PasswordMismatchException e, WebRequest request){
        ErrorDetails details = new ErrorDetails(
                e.getMessage(),
                "PASSWORD MISMATCHED",
                request.getDescription(false),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }
}
