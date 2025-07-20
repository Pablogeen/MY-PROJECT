package com.REY.Me.Service;

import com.REY.Me.DTO.ChangePasswordDTO;
import com.REY.Me.DTO.EmailValidation;
import com.REY.Me.DTO.UserLoginDTO;
import com.REY.Me.DTO.UserRequest;
import com.REY.Me.Entity.ChangePassword;
import com.REY.Me.Entity.ConfirmationToken;
import com.REY.Me.Entity.ROLE;
import com.REY.Me.Entity.User;
import com.REY.Me.Exception.EmailAlreadyExistException;
import com.REY.Me.Exception.InvalidEmailException;
import com.REY.Me.Exception.UserNotFoundException;
import com.REY.Me.Exception.UsernameAlreadyExistException;
import com.REY.Me.Repository.ChangePasswordRepo;
import com.REY.Me.Repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private UserRepository repo;
    private EmailValidation validator;
    private ConfirmationTokenService service;
    private EmailSenderService emailService;
    private ApplicationEventPublisher publisher;
    private AuthenticationManager manager;
    private ChangePasswordRepo passwordRepo;
    private JWTService jwtService;


    public UserService(UserRepository repo, EmailValidation validator, ConfirmationTokenService service, EmailSenderService emailService, ApplicationEventPublisher publisher,AuthenticationManager manager, ChangePasswordRepo passwordRepo, JWTService jwtService){
        this.repo=repo;
        this.validator=validator;
        this.service= service;
        this.emailService=emailService;
        this.publisher =publisher;
        this.manager= manager;
        this.passwordRepo= passwordRepo;
        this.jwtService= jwtService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("USERNAME NOT FOUND"));
    }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String registerUser(UserRequest request) {

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        boolean usernameExist = repo.findByUsername(request.getUsername()).isPresent();

        if(usernameExist){
            throw  new UsernameAlreadyExistException("USERNAME ALREADY TAKEN");

        }
        user.setUsername(request.getUsername());

        boolean isValid = validator.test(request.getEmail());

        if (isValid){
            throw new InvalidEmailException("INVALID EMAIL");
        }

        boolean emailExist = repo.findByEmail(request.getEmail()).isPresent();
        if (emailExist){
            throw new EmailAlreadyExistException("EMAIL ALREADY TAKEN");
        }

        user.setEmail(request.getEmail());

        user.setPassword(encoder.encode(request.getPassword()));

        user.setRole(ROLE.USER);

        repo.save(user);

        String token = UUID.randomUUID().toString();

       ConfirmationToken confirmToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        service.saveConfirmationToken(confirmToken);


        String link = "localhost:8080/api/v1/user/register/confirm?token= "+token;

            emailService.send(request.getEmail(), buildEmail(request.getFirstName(), link));
            return "REGISTERED SUCCESSFULLY";

    }

    public String registerAdmin(UserRequest request) {

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        boolean usernameExist = repo.findByUsername(request.getUsername()).isPresent();

        if(usernameExist){
            throw  new UsernameAlreadyExistException("USERNAME ALREADY TAKEN");
        }
        user.setUsername(request.getUsername());

        boolean emailExist = repo.findByEmail(request.getEmail()).isPresent();
        if (emailExist){
            throw new EmailAlreadyExistException("EMAIL ALREADY TAKEN");
        }

        boolean isValid = validator.test(request.getEmail());

        if (isValid){
            throw new InvalidEmailException("INVALID EMAIL");
        }

        user.setEmail(request.getEmail());

        user.setPassword(encoder.encode(request.getPassword()));

        user.setRole(ROLE.ADMIN);

        repo.save(user);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        service.saveConfirmationToken(confirmToken);


        String link = "localhost:8080/api/v1/user/register/confirm?token= "+token;

        emailService.send(request.getEmail(), buildEmail(request.getFirstName(), link));
        return "REGISTERED SUCCESSFULLY";

    }


    public String confirmAccount(String token) {
        ConfirmationToken confirmToken = service.getToken(token)
                .orElseThrow(()-> new IllegalStateException("TOKEN NOT FOUND"));

        if (confirmToken.getConfirmedAt() !=null){
            throw new IllegalStateException("TOKEN ALREADY CONFIRMED");
        }


        if(confirmToken.getExpires().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("TOKEN EXPIRED");
        }

            service.setConfirmationDetails(token);
        return "ACCOUNT WAS SUCCESSFULLY VERIFIED";
    }

    public int enableUser(String email){
     return   repo.enableUser(email);
    }


    public String login(@Valid UserLoginDTO login) {
        Authentication authentication=
                manager.authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));

        if (authentication.isAuthenticated()){
             jwtService.generateToken(login.getUsername());
             return "LOGGED IN SUCCESSFULLY";
        }else{
            return "INVALID CREDENTIALS";
        }


    }


    public String changePassword(@Valid ChangePasswordDTO passwordDTO) {

        User user = new User();

        ChangePassword changePassword = new ChangePassword();

        changePassword.setCurrentPassword(encoder.encode(passwordDTO.getCurrentPassword()));

        if (!encoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())){
            throw new IllegalStateException("PASSWORD INCORRECT");
        }

        changePassword.setNewPassword(encoder.encode(passwordDTO.getCurrentPassword()));

        changePassword.setUser(user);
        changePassword.setTimestamp(LocalDateTime.now());

        passwordRepo.save(changePassword);
        repo.setNewPassword(changePassword.getUser(), changePassword.getNewPassword());

        return "PASSWORD CHANGED SUCCESSFULLY";
    }


    private String buildEmail(String firstname, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + firstname + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
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
