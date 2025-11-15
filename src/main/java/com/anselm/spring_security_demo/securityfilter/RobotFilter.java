package com.anselm.spring_security_demo.securityfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


public class RobotFilter extends OncePerRequestFilter {

    private final String HEADER_NAME = "x-robot-password";

    private final AuthenticationManager authenticationManager;

    public RobotFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 0. Should the filter be executed?
        if(!Collections.list(request.getHeaderNames()).contains(HEADER_NAME)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 1. AuthenticationDecision
        var password = request.getHeader(HEADER_NAME);
        var authRequest = RobotAuthentication.unauthenticated(password);

        try {
            var authentication = authenticationManager.authenticate(authRequest);

            // immutability => don't take the existing SecurityContext and change it but you create a new one
            var newContext = SecurityContextHolder.createEmptyContext();
            // set this authentication object
            newContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(newContext);
            filterChain.doFilter(request, response);
            return;
        } catch (AuthenticationException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-type","text/plain;charset=utf-8");
            response.getWriter().println(e.getMessage());
            return;
        }

        // 2. Do the rest (cleanup)

    }
}
