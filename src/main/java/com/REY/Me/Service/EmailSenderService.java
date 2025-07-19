package com.REY.Me.Service;

import com.REY.Me.Repository.EmailSender;
import jakarta.mail.IllegalWriteException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService implements EmailSender {

    private JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender){
        this.mailSender= mailSender;
    }

    @Override
    @Async
    public void send(String to, String email)  {
        try {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        helper.setTo(to);
        helper.setSubject("VERIFY YOUR EMAIL");
        helper.setText(email, true);
        helper.setFrom("BEN & CO");

        mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
