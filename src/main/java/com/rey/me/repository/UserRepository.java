package com.rey.me.repository;

import com.rey.me.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);


    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET password =:newPassword WHERE id =:user", nativeQuery = true)
    int setNewPassword(User user, String newPassword);

    Optional<Page<User>> findByRole(String role, Pageable pageable);


    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET enabled = true WHERE email = :email", nativeQuery = true)
    int enableUser(@Param("email") String email);
}
