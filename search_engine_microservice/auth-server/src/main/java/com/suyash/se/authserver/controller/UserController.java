package com.suyash.se.authserver.controller;

import org.springframework.security.core.Authentication;import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @GetMapping("/profile")
    public Map<String, Object> getUserProfile(Authentication authentication) {
        return Map.of(
            "username", authentication.getName(),
            "authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList())
        );
    }
}