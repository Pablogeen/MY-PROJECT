package com.rey.me.controller;

import com.rey.me.dto.ChangePasswordDTO;
import com.rey.me.dto.ResetPasswordDTO;
import com.rey.me.dto.UserLoginDTO;
import com.rey.me.dto.UserRequest;
import com.rey.me.entity.ROLE;
import com.rey.me.entity.User;
import com.rey.me.serviceInterface.UserServiceInterface;
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
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequest request) throws MessagingException {
        log.info("Received request to register user: {}", request);
        String registerUser = userServiceInterface.register(request, ROLE.USER);
        log.info("User registered successfully: {}",registerUser);
        return new ResponseEntity<>(registerUser, HttpStatus.CREATED);

    }

    @GetMapping("/confirmAccount")
    public ResponseEntity<String>confirmAccount(@RequestParam String token){
        return new ResponseEntity<>(userServiceInterface.confirmAccount(token), HttpStatus.OK);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody UserRequest request) throws MessagingException {
        String registerAdmin= userServiceInterface.register(request, ROLE.ADMIN);
        return new ResponseEntity<>(registerAdmin, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO login){
        return new ResponseEntity<>(userServiceInterface.login(login), HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDTO passwordDTO, @AuthenticationPrincipal User user){
        return new ResponseEntity<>(userServiceInterface.changePassword(passwordDTO, user), HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String>resetPassword(@Valid @RequestBody ResetPasswordDTO resetPassword) throws MessagingException {
        return new ResponseEntity<>(userServiceInterface.resetPassword(resetPassword), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<Page<User>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userServiceInterface.getAllUsers(pageable), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(userServiceInterface.getUserById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{role}")
    public ResponseEntity<List<User>> getRoleByUser(@PathVariable String role, @RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(userServiceInterface.getUsersByRole(role, pageable), HttpStatus.OK);
    }




}
