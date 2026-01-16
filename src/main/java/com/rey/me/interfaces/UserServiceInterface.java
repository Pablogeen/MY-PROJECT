package com.rey.me.interfaces;

import com.rey.me.dto.*;
import com.rey.me.entity.User;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface UserServiceInterface {
    UserResponseDto register(@Valid UserRequestDto request)throws MessagingException;

    String confirmAccount(String token);

    String login(@Valid UserLoginDTO login);

    String changePassword(@Valid ChangePasswordDTO passwordDTO, User user);

    String resetPassword(@Valid ResetPasswordDTO resetPassword)throws MessagingException;

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    UserResponseDto getUserById(Long id);

    Page<UserResponseDto> getUsersByRole(String role, Pageable pageable);

    UserResponseDto assignAdminRole(Long id);

    UserResponseDto revokeAdminRole(Long id);
}
