package com.rey.me.service;

import com.rey.me.dto.ChangePasswordDTO;
import com.rey.me.dto.ResetPasswordDTO;
import com.rey.me.dto.UserLoginDTO;
import com.rey.me.dto.UserRequestDto;
import com.rey.me.entity.*;
import com.rey.me.exception.*;
import com.rey.me.helper.EmailBuilder;
import com.rey.me.helper.UserHelper;
import com.rey.me.interfaces.UserServiceInterface;
import com.rey.me.repository.ChangePasswordRepo;
import com.rey.me.repository.ResetPasswordRepository;
import com.rey.me.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserServiceInterface {

    private final UserRepository repo;
    private final EmailValidation validator;
    private final ConfirmationTokenService service;
    private final EmailSenderService emailService;
    private final AuthenticationManager manager;
    private final ChangePasswordRepo passwordRepo;
    private final JWTService jwtService;
    private final ResetPasswordRepository resetRepo;
    private final BCryptPasswordEncoder encoder;
    private final UserHelper userHelper;
    private final EmailBuilder emailBuilder;


    @Transactional
    @Async
    public String register(UserRequestDto request) throws MessagingException {

        User user = new User();
        user.setFirstname(request.getFirstname().strip());
        user.setLastname(request.getLastname().strip());

        boolean usernameExist = repo.findByUsername(request.getUsername().strip()).isPresent();

        if(usernameExist){
            throw  new UsernameAlreadyExistException("USERNAME ALREADY TAKEN");
        }
        user.setUsername(request.getUsername().strip());

        boolean isValid = validator.test(request.getEmail().strip());

        if (!isValid){
            throw new InvalidEmailException("INVALID EMAIL");
        }

        boolean emailExist = repo.findByEmail(request.getEmail().strip()).isPresent();

        if (emailExist){
            throw new EmailAlreadyExistException("EMAIL ALREADY TAKEN");
        }

        user.setEmail(request.getEmail().strip());

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new PasswordMismatchException("PASSWORD MISMATCHED");
        }

        user.setPassword(encoder.encode(request.getPassword().strip()));
        user.setRole(ROLE.USER);
        repo.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        userHelper.sendConfirmationToken(confirmationToken, user);
        log.info("Confirmed Token Successfully");

            return "REGISTERED SUCCESSFULLY";

    }

    @Transactional
    public String confirmAccount(String token) {
        ConfirmationToken confirmToken = service.getToken(token)
                .orElseThrow(()-> new IllegalStateException("TOKEN NOT FOUND"));
        log.info("Confirmation Token from the db");

        if (confirmToken.getConfirmedAt() !=null){
            log.error("Token already confirmed");
            throw new IllegalStateException("TOKEN ALREADY CONFIRMED");
        }

        if(confirmToken.getExpires().isBefore(LocalDateTime.now())){
            log.error("Token expired");
            throw new IllegalStateException("TOKEN EXPIRED");
        }

            service.setConfirmationDetails(token);
        
        String email = confirmToken.getUser().getEmail();
        enableUser(email);
        return "ACCOUNT WAS SUCCESSFULLY VERIFIED";
    }

    public int enableUser(String email){
     return repo.enableUser(email);
    }


    public String login(@Valid UserLoginDTO login) {
        Authentication authentication=
                manager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
        if (authentication.isAuthenticated()){
             return jwtService.generateToken(login.getUsername().strip());
        }else{
            return "INVALID CREDENTIALS";
        }


    }

        @Transactional
    public String changePassword(@Valid ChangePasswordDTO passwordDTO, User user) {

        ChangePassword changePassword = new ChangePassword();

        changePassword.setCurrentPassword(encoder.encode(passwordDTO.getCurrentPassword().strip()));

        if (!encoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())){
            throw new IllegalStateException("PASSWORD INCORRECT");
        }

        changePassword.setNewPassword(encoder.encode(passwordDTO.getCurrentPassword()));

        changePassword.setUser(user);
        changePassword.setTimestamp(LocalDateTime.now());

        passwordRepo.save(changePassword);
        repo.setNewPassword(changePassword.getUser(), changePassword.
                getNewPassword());

        return "PASSWORD CHANGED SUCCESSFULLY";
    }

    @Transactional
    @Async
    public String resetPassword(@Valid ResetPasswordDTO resetPassword) throws MessagingException {
       boolean emailExist = repo.findByEmail(resetPassword.getEmail().strip()).isPresent();

       boolean isValidEmail = validator.test(resetPassword.getEmail().strip());

       if (!isValidEmail){
           throw new InvalidEmailException("INVALID EMAIL");
       }
        User user = new User();

       if(!emailExist && user.isEnabled()){
           throw new InvalidEmailException("INVALID EMAIL");

       }

      ResetPassword password = new ResetPassword();
       password.setEmail(resetPassword.getEmail().strip());

       Random random = new Random();
       String token = String.valueOf(100000+ random.nextInt(90000));
       password.setToken(encoder.encode(token));
       password.setCreatedAt(LocalDateTime.now());
       password.setExpiration(LocalDateTime.now().plusMinutes(5));

       resetRepo.save(password);

         user = repo.findByEmail(resetPassword.getEmail())
                 .orElseThrow(()-> new UserNotFoundException("USER NOT FOUND"));

       emailService.sendResetPasswordToken(resetPassword.getEmail(), emailBuilder.resetPasswordEmail(user.getFirstname(), token));


       ResetPassword reset = resetRepo.findByToken(resetPassword.getToken())
               .orElseThrow(()-> new IllegalStateException("INVALID TOKEN"));

       if (LocalDateTime.now().isAfter(reset.getExpiration())){
           throw new IllegalStateException("TOKEN EXPIRED");
       }

       if (!encoder.matches(resetPassword.getToken(),token)){
           throw new IllegalStateException("WRONG TOKEN");
       }

       user.setPassword(encoder.encode(resetPassword.getNewPassword()));

       return "RESET PASSWORD SUCCESSFULLY";

    }

    public List<Page<User>> getAllUsers(Pageable pageable) {
        return Collections.singletonList(repo.findAll(pageable));
    }

    public User getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(()->new UserNotFoundException("USER NOT FOUND"));
    }


    public List<User> getUsersByRole(String role, Pageable pageable) {
        return repo.findByRole(role, pageable);
    }








}
