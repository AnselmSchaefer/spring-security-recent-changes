package com.anselm.spring_security_demo.config;

import com.anselm.spring_security_demo.authentication.AnselmAuthenticationProvider;
import com.anselm.spring_security_demo.authentication.RobotAuthenticationProvider;
import com.anselm.spring_security_demo.securityfilter.RobotFilter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Provider;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public DefaultSecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationEventPublisher publisher) throws Exception {

        // both is allowed as header value
        var authManager = new ProviderManager(
                new RobotAuthenticationProvider(List.of("beep-boop", "boop-beep")));
        authManager.setAuthenticationEventPublisher(publisher);

        return http
                .authorizeHttpRequests(authorizeConfig -> {
                    authorizeConfig.requestMatchers("/").permitAll();
                    authorizeConfig.requestMatchers("/error").permitAll();
                    authorizeConfig.anyRequest().authenticated();
                })
                .formLogin(withDefaults())
                // go to Google to get them: https://console.cloud.google.com  => you have to specify a redirect URI like this: http://localhost:8080/login/oauth2/code/google
                .oauth2Login(withDefaults())
                // in powershell:  Invoke-WebRequest -Uri "http://localhost:8080/api/private" -Headers @{"x-robot-password" = "beep-boop"} -Method Get -Verbose
                // best practice instead UsernamePasswordAuthenticationFilter: FilterSecurityInterceptor.class
                .addFilterBefore(new RobotFilter(authManager), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(new AnselmAuthenticationProvider())
                .build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.builder()
                        .username("user")
                        .password("{bcrypt}$2a$10$e23WF/KW7f7ST/zRxUDjbeWuUtTFrLdLKLiSB/Rv25B0KOo0mzMOS")
                        .authorities("ROLE_specialrole")
                        .build()
        );
    }

    @Bean
    public ApplicationListener<AuthenticationSuccessEvent> successListener() {
        return event -> {
            System.out.println(String.format("Success [%s] %s",
                    event.getAuthentication().getClass().getSimpleName(),
                    event.getAuthentication().getName()));
        };
    }
}
