package com.rey.me.service;

import com.rey.me.entity.ConfirmationToken;
import com.rey.me.repository.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationTokenService {

    private ConfirmationTokenRepository repo;

    public ConfirmationTokenService(ConfirmationTokenRepository repo){
        this.repo= repo;
    }


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
