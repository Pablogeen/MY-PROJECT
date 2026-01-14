package com.rey.me.interfaces;

import com.rey.me.entity.User;
import jakarta.mail.MessagingException;

public interface EmailSender {

    void sendConfirmationEmail(String to, String email) throws MessagingException;

    void sendCVUploadNotification(String to, String filename, String filePath, User applicant) throws MessagingException;

    void sendResetPasswordToken(String to, String email) throws MessagingException;
}
