package com.REY.Me.Exception;

public class EmailSendingFailedException extends RuntimeException{

    public EmailSendingFailedException(String message) {
        super(message);
    }
}
