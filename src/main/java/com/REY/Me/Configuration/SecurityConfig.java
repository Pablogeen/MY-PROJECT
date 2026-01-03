package com.REY.Me.Configuration;

import com.REY.Me.Service.UserService;
import jakarta.servlet.FilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private UserService service;

    @Autowired
    private JWTFilter filter;

    @Bean
      public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          http.csrf(customizer->customizer.disable());
          http.authorizeHttpRequests(request->request.requestMatchers("/api/v1/user").permitAll()
                          .requestMatchers("/api/v1/job/add").hasAnyAuthority("ADMIN","USER")
                         .requestMatchers("/api/v1/job").hasAnyAuthority("ADMIN","USER")
                          .requestMatchers("/api/v1/job/id").hasAnyAuthority("ADMIN","USER")
                          .requestMatchers("/api/v1/job/delete/id").hasAnyAuthority("hasAuthority('ADMIN') or @JobSecurity.isJobOwner(authentication, #id)")
                          .requestMatchers("/api/v1/job/update/id").hasAnyAuthority("hasAuthority('ADMIN') or @JobSecurity.isJobOwner(authentication, #id)")
                          .requestMatchers("/api/v1/job/search").hasAnyAuthority("ADMIN","USER")
                         .requestMatchers("/api/v1/job/category").hasAnyAuthority("ADMIN","USER")
                          .requestMatchers("/api/v1/job/apply").hasAuthority("USER")
                        .requestMatchers("/api/v1/NewsLetter/post").hasAnyAuthority("ADMIN","USER")
                  .requestMatchers("/api/v1/NewsLetter").hasAnyAuthority("ADMIN","USER")
                  .requestMatchers("/api/v1/job/NewsLetter/id").hasAnyAuthority("ADMIN","USER")
                  .requestMatchers("/api/v1/NewsLetter/delete").hasAnyAuthority("ADMIN","USER").anyRequest().authenticated());

          http.httpBasic(Customizer.withDefaults());
          http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
          http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
          http.logout(logout -> logout
                  .logoutUrl("/api/logout")
                  .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                  .invalidateHttpSession(true)
                  .deleteCookies("JSESSIONID")
          );

          return http.build();
      }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

      @Bean
      public AuthenticationProvider provider(){
          DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
          authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
          authProvider.setUserDetailsService(service);
          return authProvider;
      }

      @Bean
      public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
      }
}


