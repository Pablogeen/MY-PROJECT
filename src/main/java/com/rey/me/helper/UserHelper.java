package com.rey.me.helper;

import com.rey.me.entity.ConfirmationToken;
import com.rey.me.entity.User;
import com.rey.me.service.ConfirmationTokenService;
import com.rey.me.service.EmailSenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserHelper {

    private final ConfirmationTokenService tokenService;
    private final EmailSenderService emailService;
    private final EmailBuilder emailBuilder;

    public void sendConfirmationToken(ConfirmationToken confirmationToken, User user) throws MessagingException {

        SecureRandom random = new SecureRandom();
        int code = random.nextInt(999999);
       String token = String.format("%06d", code);

        ConfirmationToken saveConfirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );


        tokenService.saveConfirmationToken(saveConfirmationToken);
        log.info("Confirmation Details saved successfully");

        emailService.sendConfirmationEmail(user.getEmail(), emailBuilder.buildEmail(user.getFirstname(), token));
        log.info("Email sent successfully");

    }

}
