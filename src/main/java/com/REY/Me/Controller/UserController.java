package com.REY.Me.Controller;

import com.REY.Me.DTO.ChangePasswordDTO;
import com.REY.Me.DTO.UserLoginDTO;
import com.REY.Me.DTO.UserRequest;
import com.REY.Me.Entity.User;
import com.REY.Me.Service.ConfirmationTokenService;
import com.REY.Me.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private UserService service;


    public UserController(UserService service){
        this.service=service;
    }

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequest request){
        return new ResponseEntity<>(service.registerUser(request), HttpStatus.CREATED);

    }

    @GetMapping("/confirmAccount")
    public ResponseEntity<String>confirmAccount(@RequestParam String token){
        return new ResponseEntity<>(service.confirmAccount(token), HttpStatus.OK);
    }

    @PostMapping("/admin/register")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody UserRequest request){
        return new ResponseEntity<>(service.registerAdmin(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO login){
        return new ResponseEntity<>(service.login(login), HttpStatus.OK);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDTO passwordDTO){
        return new ResponseEntity<>(service.changePassword(passwordDTO), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<Page<User>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(service.getAllUsers(pageable), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(service.getUserById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/{role}")
    public ResponseEntity<List<User>> getRoleByUser(@PathVariable String role, @RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(service.getUsersByRole(role, pageable), HttpStatus.OK);
    }




}
