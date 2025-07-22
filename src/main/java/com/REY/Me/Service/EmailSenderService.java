package com.REY.Me.Service;

import com.REY.Me.Entity.User;
import com.REY.Me.Repository.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService implements EmailSender {

    private Authentication authentication;

        private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderService.class);

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
            LOGGER.error("THERE WAS AN ERROR CREATING THE EMAIL", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async
    public void sendWithEmail(String to, String filename, String filePath) throws MessagingException {

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
}
