package com.REY.Me.Repository;

import com.REY.Me.Entity.ChangePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangePasswordRepo extends JpaRepository<ChangePassword, Long> {
}
