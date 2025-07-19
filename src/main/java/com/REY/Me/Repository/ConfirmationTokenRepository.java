package com.REY.Me.Repository;

import com.REY.Me.Entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);


    @Modifying
    @Transactional
    @Query(value = "UPDATE confirmation_token SET confirmed_at =:createdAt WHERE token=:token", nativeQuery = true)
    int updateConfirmationDetails(String token, LocalDateTime createdAt);
}
