package com.rey.me.service;

import com.rey.me.entity.User;
import com.rey.me.interfaces.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService implements EmailSender {


    private final JavaMailSender mailSender;

    @Value("${SMTP_USERNAME}")
    private String email;


@Override
public void sendConfirmationEmail(String receiverEmail, String message) throws MessagingException  {

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
    helper.setTo(receiverEmail);
    helper.setSubject("VERIFY YOUR EMAIL");
    helper.setText(message, true);
    helper.setFrom(email);

    mailSender.send(mimeMessage);
    log.info("Email Sent Successfully");


}

@Override
public void sendCVUploadNotification(String recipientEmail, String filename, String filePath) throws MessagingException {
    // Get authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
        throw new IllegalStateException("No authenticated user found");
    }

    User user = (User) authentication.getPrincipal();
    String applicantEmail = user.getEmail();

    if (applicantEmail == null) {
        throw new IllegalStateException("User email is not available");
    }

    // Verify file exists
    File cvFile = new File(filePath);
    if (!cvFile.exists()) {
        throw new IllegalArgumentException("CV file not found at path: " + filePath);
    }


    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

    helper.setFrom(applicantEmail);
    helper.setReplyTo(applicantEmail);
    helper.setSubject("JOB APPLICATION");
    helper.setTo(recipientEmail);

    String emailBody = String.format(
            "Dear Hiring Manager,\n\n" +
                    "You have received a new job application.\n\n" +
                    "Email: %s\n" +
                    "CV File: %s\n\n" +
                    "Please find the attached Curriculum Vitae (CV).\n\n" +
                    "Best regards,\n" +
                    "Your Application System",
             applicantEmail, filename
    );

    helper.setText(emailBody);
    helper.addAttachment(filename, new FileSystemResource(filePath));

    mailSender.send(mimeMessage);
    log.info("CV upload notification sent to {} for applicant {}", recipientEmail, applicantEmail);
}

@Override
@Async
public void sendResetPasswordToken(String receiverEmail, String message) throws MessagingException {

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
    helper.setTo(receiverEmail);
    helper.setFrom(email);
    helper.setSubject("RESET PASSWORD");
    helper.setText("Copy and paste this code to set a new password");

    mailSender.send(mimeMessage);

}
    }
