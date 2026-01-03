package com.REY.Me.Repository;

import jakarta.mail.MessagingException;

public interface EmailSender {

    void sendConfirmationEmail(String to, String email) throws MessagingException;

    void sendFileWithEmail(String to, String filename, String filePath) throws MessagingException;

    void sendResetPasswordToken(String to, String email) throws MessagingException;
}
