package com.rey.me.service;

import com.rey.me.dto.*;
import com.rey.me.entity.*;
import com.rey.me.exception.*;
import com.rey.me.repository.ChangePasswordRepo;
import com.rey.me.repository.ResetPasswordRepository;
import com.rey.me.repository.UserRepository;
import com.rey.me.serviceInterface.UserServiceInterface;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserServiceInterface {

    private final UserRepository repo;
    private final EmailValidation validator;
    private final ConfirmationTokenService service;
    private final EmailSenderService emailService;
    private final AuthenticationManager manager;
    private final ChangePasswordRepo passwordRepo;
    private final JWTService jwtService;
    private final ResetPasswordRepository resetRepo;
    private final BCryptPasswordEncoder encoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("USERNAME NOT FOUND"));

    }


    @Transactional
    @Async
    public String register(UserRequest request, ROLE role) throws MessagingException {

        User user = new User();
        user.setFirstname(request.getFirstname().strip());
        user.setLastname(request.getLastname().strip());

        boolean usernameExist = repo.findByUsername(request.getUsername()).isPresent();

        if(usernameExist){
            throw  new UsernameAlreadyExistException("USERNAME ALREADY TAKEN");

        }
        user.setUsername(request.getUsername().strip());

        boolean isValid = validator.test(request.getEmail().trim());

        if (!isValid){
            throw new InvalidEmailException("INVALID EMAIL");
        }

        boolean emailExist = repo.findByEmail(request.getEmail().trim()).isPresent();
        if (emailExist){
            throw new EmailAlreadyExistException("EMAIL ALREADY TAKEN");
        }

        user.setEmail(request.getEmail().strip());

        if(request.getPassword().equals(request.getConfirmPassword())){
            throw new PasswordMismatchException("PASSWORD MISMATCHED");
        }

        user.setPassword(encoder.encode(request.getPassword().strip()));


        user.setRole(role);

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

            emailService.sendConfirmationEmail(request.getEmail(), buildEmail(request.getFirstname(), link));
            return "REGISTERED SUCCESSFULLY";

    }




    @Transactional
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

       emailService.sendResetPasswordToken(resetPassword.getEmail(), resetPasswordEmail(user.getFirstname(), token));


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


    private String resetPasswordEmail(String firstname, String token) {
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
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">RESET YOUR PASSWORD</span>\n" +
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
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + firstname + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">This is your verification code. For security reasons, do not share this code. </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <h2>\"" + token + "\"</h2> </p></blockquote>\n Code expires in 5 minutes. <p>Kindly Ignore this message if you did not initiate this action.</p>" +
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






}
