package com.rey.me.repository;

import com.rey.me.entity.ChangePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangePasswordRepo extends JpaRepository<ChangePassword, Long> {
}
