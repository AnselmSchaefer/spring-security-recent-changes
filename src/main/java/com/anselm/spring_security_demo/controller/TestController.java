package com.anselm.spring_security_demo.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TestController {

    private static final Log log = LogFactory.getLog(TestController.class);

    @GetMapping("/test")
    public String getHello() {
        return "Hello World :)";
    }

    @GetMapping("/private")
    public String getPrivateHello(Authentication authentication) {
        return "Hello " + this.getName(authentication);
    }

    private String getName(Authentication authentication) {
        return Optional.of(authentication.getPrincipal())
                .filter(OidcUser.class::isInstance)
                .map(OidcUser.class::cast)
                .map(OidcUser::getEmail)
                .orElseGet(authentication::getName);
    }

}
