package com.rey.me.service;

import com.rey.me.entity.User;
import com.rey.me.interfaces.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService implements EmailSender {

    private Authentication authentication;
    private final JavaMailSender mailSender;



@Override
@Async
public void sendConfirmationEmail(String to, String email) throws MessagingException  {

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
    helper.setTo(to);
    helper.setSubject("VERIFY YOUR EMAIL");
    helper.setText(email, true);
    helper.setFrom("BEN & CO");

    mailSender.send(mimeMessage);
    log.info("Email Sent Successfully");


}

@Override
@Async
public void sendFileWithEmail(String to, String filename, String filePath) throws MessagingException {

    User user = (User) authentication.getPrincipal();
    String from = user.getEmail();

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
    helper.setFrom(from);
    helper.setSubject("JOB APPLICATION");
    helper.setTo(to);
    helper.setText("Please find the attached File of my Curriculum Vitae(CV)");
    helper.addAttachment(filename, new FileSystemResource(filePath));
    mailSender.send(mimeMessage);


}

@Override
@Async
public void sendResetPasswordToken(String to, String email) throws MessagingException {

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
    helper.setTo(to);
    helper.setFrom("BEN & CO");
    helper.setSubject("RESET PASSWORD");
    helper.setText("Copy and paste this code to set a new password");

    mailSender.send(mimeMessage);

}
    }
