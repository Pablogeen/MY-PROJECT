package com.REY.Me.Configuration;

import com.REY.Me.Service.JWTService;
import com.REY.Me.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private JWTService service;
    private ApplicationContext context;

    public JWTFilter(JWTService service, ApplicationContext context){
        this.service= service;
        this.context=context;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String authHeader =request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer")){
            token = authHeader.substring(7);
            username= service.extractUserName(token);
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            UserDetails userDetails = context.getBean(UserService.class).loadUserByUsername(username);

            if (service.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken token1 =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                token1.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token1);
            }
        }
        filterChain.doFilter(request, response);


    }
}
