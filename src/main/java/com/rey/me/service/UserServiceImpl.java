package com.rey.me.service;

import com.rey.me.dto.*;
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
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserServiceInterface {

    private final UserRepository userRepo;
    private final ConfirmationTokenService service;
    private final EmailSenderService emailService;
    private final AuthenticationManager manager;
    private final ChangePasswordRepo passwordRepo;
    private final JWTService jwtService;
    private final ResetPasswordRepository resetRepo;
    private final BCryptPasswordEncoder encoder;
    private final UserHelper userHelper;
    private final EmailBuilder emailBuilder;
    private final ModelMapper modelMapper;

    @Transactional
    @CachePut(value = "users", key = "#result.id")
    public String register(UserRequestDto request) throws MessagingException {

        User user = new User();
        user.setFirstname(request.getFirstname().strip());
        user.setLastname(request.getLastname().strip());

        boolean usernameExist = userRepo.findByUsername(request.getUsername().strip()).isPresent();
        log.info("Check if username is present: {}",usernameExist);

        if(usernameExist){
            throw  new UsernameAlreadyExistException("USERNAME ALREADY TAKEN");
        }
        user.setUsername(request.getUsername().strip());

        boolean emailExist = userRepo.findByEmail(request.getEmail().strip()).isPresent();
        log.info("Check if email is present: {}",emailExist);

        if (emailExist){
            throw new EmailAlreadyExistException("EMAIL ALREADY TAKEN");
        }

        user.setEmail(request.getEmail().strip());

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new PasswordMismatchException("PASSWORD MISMATCHED");
        }

        user.setPassword(encoder.encode(request.getPassword().strip()));
        user.setRole(ROLE.USER);
        userRepo.save(user);
        log.info("User saved successfully: {}",user.getUsername());

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
     return userRepo.enableUser(email);
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
        userRepo.setNewPassword(changePassword.getUser(), changePassword.
                getNewPassword());

        return "PASSWORD CHANGED SUCCESSFULLY";
    }

    @Transactional
    public String resetPassword(@Valid ResetPasswordDTO resetPassword) throws MessagingException {

       boolean emailExist = userRepo.findByEmail(resetPassword.getEmail().strip()).isPresent();

        User user = new User();

       if(!emailExist && user.isEnabled()){
           throw new InvalidEmailException("INVALID EMAIL");
       }

      ResetPassword password = new ResetPassword();
       password.setEmail(resetPassword.getEmail().strip());

        SecureRandom random = new SecureRandom();
        int code = random.nextInt(999999);
        String token = String.format("%06d", code);

       password.setToken(encoder.encode(token));

       password.setCreatedAt(LocalDateTime.now());
       password.setExpiration(LocalDateTime.now().plusMinutes(5));

       resetRepo.save(password);

         user = userRepo.findByEmail(resetPassword.getEmail())
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

    @Cacheable(value = "allUsers", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepo.findAll(pageable);
        log.info("Gotten all users from the database");
        Page<UserResponseDto> userResponseDto =userPage
                        .map(user -> modelMapper.map(user, UserResponseDto.class));
        log.info("Mapped user into UserResponseDto: {}",userResponseDto);

        return userResponseDto;
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponseDto getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(()-> new UserNotFoundException("USER NOT FOUND"));
        log.info("User found with id: {}",user.getId());
            UserResponseDto userResponse = modelMapper.map(user, UserResponseDto.class);
            log.info("Mapped user to userResponse: {}",userResponse);
        return userResponse;
    }

    @Cacheable(value = "usersByRole", key = "#role + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserResponseDto> getUsersByRole(String role, Pageable pageable) {
    Page<User> userRole =  userRepo.findByRole(role, pageable)
                .orElseThrow(() -> new IllegalStateException("ROLE NOT FOUND"));
        log.info("Users with roles found in the database");
        Page<UserResponseDto> roleResponse =
                userRole.map(userr -> modelMapper.map(userr, UserResponseDto.class));
        log.info("Mapped user role to Role Response: {}",roleResponse);
        return roleResponse;
    }

    @Override
    @Caching(
            put = @CachePut(value = "users", key = "#id"),
            evict = {
                    @CacheEvict(value = "allUsers", allEntries = true),
                    @CacheEvict(value = "usersByRole", allEntries = true)
            }
    )
    public UserResponseDto assignAdminRole(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(()-> new UserNotFoundException("USER NOT FOUND"));
        log.info("User with id: {} found",user.getId());

        if (ROLE.ADMIN.equals(user.getRole())) {
            log.error("User already has admin privileges");
            throw new IllegalStateException("User already has admin privileges");
        }

        user.setRole(ROLE.ADMIN);
        log.info("Role set to Admin");

        User savedUser = userRepo.save(user);
        log.info("Role assigned successfully");

        UserResponseDto userResponse = modelMapper.map(savedUser, UserResponseDto.class);
        log.info("Mapped saved user into response dto");
        return userResponse;
    }

    @Override
    @Caching(
            put = @CachePut(value = "users", key = "#id"),
            evict = {
                    @CacheEvict(value = "allUsers", allEntries = true),
                    @CacheEvict(value = "usersByRole", allEntries = true)
            }
    )
    public UserResponseDto revokeAdminRole(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(()-> new UserNotFoundException("USER NOT FOUND"));
        log.info("User id: {} found",user.getId());

        if (ROLE.USER.equals(user.getRole())){
            log.error("User already has User privileges");
            throw new IllegalStateException("User already has User privileges");
        }

        user.setRole(ROLE.USER);
        log.info("Role revoked to User");

        User savedUser = userRepo.save(user);
        log.info("Role saved to user");

        UserResponseDto userResponse = modelMapper.map(savedUser, UserResponseDto.class);
        log.info("Saved User into UserResponse");
        return userResponse;
    }
}


