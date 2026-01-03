package com.rey.me.exception;

public class UsernameAlreadyExistException extends RuntimeException{

    public UsernameAlreadyExistException(String message) {
        super(message);
    }
}
