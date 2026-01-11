package com.rey.me.helper;

import com.rey.me.entity.ConfirmationToken;
import com.rey.me.entity.User;
import com.rey.me.service.ConfirmationTokenService;
import com.rey.me.service.EmailSenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserHelper {

    private final ConfirmationTokenService tokenService;
    private final EmailSenderService emailService;
    private final EmailBuilder emailBuilder;

    public void sendConfirmationToken(ConfirmationToken confirmationToken, User user) throws MessagingException {

        String token = UUID.randomUUID().toString();
        log.info("Generated Token: {}",token);

        ConfirmationToken saveConfirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );


        tokenService.saveConfirmationToken(saveConfirmationToken);
        log.info("Confirmation Details saved successfully");

        String link = "localhost:8080/api/v1/user/register/confirm?token= "+token;
        log.info("Token generated");

        emailService.sendConfirmationEmail(user.getEmail(), emailBuilder.buildEmail(user.getFirstname(), link));
        log.info("Email sent successfully");

    }

}
