package com.REY.Me.Repository;

import jakarta.mail.MessagingException;

public interface EmailSender {

    void send(String to, String email) throws MessagingException;

    void sendWithEmail(String to, String filename, String filePath) throws MessagingException;
}
