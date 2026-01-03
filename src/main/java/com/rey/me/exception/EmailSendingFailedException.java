package com.rey.me.exception;

public class EmailSendingFailedException extends RuntimeException{

    public EmailSendingFailedException(String message) {
        super(message);
    }
}
