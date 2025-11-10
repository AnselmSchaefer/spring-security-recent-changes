package com.anselm.spring_security_demo.securityfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RobotFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 1. AuthenticationDecision
        var password = request.getHeader("x-robot-password");
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

        // 2. Do the Rest

    }
}
