package com.rey.me.controller;

import com.rey.me.dto.*;
import com.rey.me.entity.User;
import com.rey.me.interfaces.UserServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceInterface userServiceInterface;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody
                                                   UserRequestDto request) throws MessagingException {
        log.info("Received request to register user");
        UserResponseDto registerUser = userServiceInterface.register(request);
        log.info("User registered successfully: {}",registerUser);
        return new ResponseEntity<>(registerUser, HttpStatus.CREATED);
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<String>confirmAccount(@RequestParam String token){
        return new ResponseEntity<>(userServiceInterface.confirmAccount(token), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/assign-admin/{id}")
    public ResponseEntity<UserResponseDto> assignAdminRole(@PathVariable Long id) {
        log.info("Assigning Admin role to a user");
        UserResponseDto assignRole= userServiceInterface.assignAdminRole(id);
        log.info("Admin role assigned to user");
        return new ResponseEntity<>(assignRole, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/revoke-admin/{id}")
    public ResponseEntity<UserResponseDto> revokeAdminRole(@PathVariable Long id) {
        log.info("Revoking Admin role to a User");
        UserResponseDto revokedRole= userServiceInterface.revokeAdminRole(id);
        log.info("Admin role revoked");
        return new ResponseEntity<>(revokedRole, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO loginDto){
        log.info("Request made to login");
        String loginResponse = userServiceInterface.login(loginDto);
        log.info("Successfully logged in");
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDTO passwordDTO,
                                                 @AuthenticationPrincipal User user){
        log.info("Request made to changePassword");
        String response = userServiceInterface.changePassword(passwordDTO, user);
        log.info("Password changed successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/reset-password")
    public ResponseEntity<String>resetPassword(
            @Valid @RequestBody ResetPasswordDTO resetPassword) throws MessagingException {
        log.info("Request made to resetPassword");
        String response = userServiceInterface.resetPassword(resetPassword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public PageResponse<UserResponseDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponseDto> userPage = userServiceInterface.getAllUsers(pageable);
        log.info("Got {} users on page {}", userPage.getNumberOfElements(), page);
            return new PageResponse<>(userPage.getContent(), userPage.getNumber(),
                    userPage.getSize(), userPage.getTotalElements(), userPage.getTotalPages());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        log.info("Getting the details of user with id: {}",id);
        UserResponseDto userResponse = userServiceInterface.getUserById(id);
        log.info("Got the user: {}",userResponse);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UserResponseDto>> getRoleByUser(
            @PathVariable String role, @RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponseDto> roleResponse = userServiceInterface.getUsersByRole(role, pageable);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }




}
