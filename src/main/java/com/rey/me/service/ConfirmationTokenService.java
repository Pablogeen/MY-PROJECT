package com.rey.me.service;

import com.rey.me.entity.ConfirmationToken;
import com.rey.me.repository.ConfirmationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository repo;


    public void saveConfirmationToken(ConfirmationToken confirmToken) {
        repo.save(confirmToken);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return repo.findByToken(token);
    }

    public int setConfirmationDetails(String token) {
      return  repo.updateConfirmationDetails(token, LocalDateTime.now());
    }
}
