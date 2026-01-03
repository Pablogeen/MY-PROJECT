package com.rey.me.repository;

import com.rey.me.entity.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {

    @Query(value = "SELECT * from reset_password WHERE token =:token", nativeQuery = true)
    Optional<ResetPassword> findByToken(String token);
}
