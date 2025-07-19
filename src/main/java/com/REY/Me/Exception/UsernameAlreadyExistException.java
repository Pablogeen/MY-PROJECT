package com.REY.Me.Exception;

public class UsernameAlreadyExistException extends RuntimeException{

    public UsernameAlreadyExistException(String message) {
        super(message);
    }
}
