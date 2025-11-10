package com.anselm.spring_security_demo.securityfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


public class RobotFilter extends OncePerRequestFilter {

    private final String HEADER_NAME = "x-robot-password";

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
        if(!"beep-boop".equals(password)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-type", "text/plain;charset=utf-8");
            response.getWriter().println("You are not Ms Robot");
            return;
        }

        var newContext = SecurityContextHolder.createEmptyContext();
        newContext.setAuthentication(
                new RobotAuthentication()
        );
        SecurityContextHolder.setContext(newContext);
        filterChain.doFilter(request, response);
        return;

        // 2. Do the rest (cleanup)

    }
}
