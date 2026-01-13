package com.rey.me.interfaces;

import jakarta.mail.MessagingException;

public interface EmailSender {

    void sendConfirmationEmail(String to, String email) throws MessagingException;

    void sendCVUploadNotification(String to, String filename, String filePath) throws MessagingException;

    void sendResetPasswordToken(String to, String email) throws MessagingException;
}
