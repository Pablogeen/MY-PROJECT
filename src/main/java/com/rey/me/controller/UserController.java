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

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceInterface userServiceInterface;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody
                                                   UserRequestDto request) throws MessagingException {
        log.info("Received request to register user");
        String registerUser = userServiceInterface.register(request);
        log.info("User registered successfully: {}",registerUser);
        return new ResponseEntity<>(registerUser, HttpStatus.CREATED);

    }

    @GetMapping("/confirmAccount")
    public ResponseEntity<String>confirmAccount(@RequestParam String token){
        return new ResponseEntity<>(userServiceInterface.confirmAccount(token), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/assignAdmin")
    public ResponseEntity<String> assignAdminRole(@PathVariable Long id) {
        log.info("Assigning Admin role to a user");
        String assignRole= userServiceInterface.assignAdminRole(id);
        log.info("Admin role assigned to user");
        return new ResponseEntity<>(assignRole, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/revokeAdmin")
    public ResponseEntity<String> revokeAdminRole(@PathVariable Long id) {
        log.info("Revoking Admin role to a User");
        String revokedRole= userServiceInterface.revokeAdminRole(id);
        log.info("Admin role revoked");
        return new ResponseEntity<>(revokedRole, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO login){
        return new ResponseEntity<>(userServiceInterface.login(login), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDTO passwordDTO,
                                                 @AuthenticationPrincipal User user){
        return new ResponseEntity<>(userServiceInterface.changePassword(passwordDTO, user), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/resetPassword")
    public ResponseEntity<String>resetPassword(@Valid @RequestBody ResetPasswordDTO resetPassword) throws MessagingException {
        return new ResponseEntity<>(userServiceInterface.resetPassword(resetPassword), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponseDto> userPage = userServiceInterface.getAllUsers(pageable);

        log.info("Got {} users on page {}", userPage.getNumberOfElements(), page);
        return new ResponseEntity<>(userPage, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        log.info("Getting the details of user with id: {}",id);
        UserResponseDto userResponse = userServiceInterface.getUserById(id);
        log.info("Got the user: {}",userResponse);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{role}")
    public ResponseEntity<List<User>> getRoleByUser(@PathVariable String role, @RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userServiceInterface.getUsersByRole(role, pageable), HttpStatus.OK);
    }




}
