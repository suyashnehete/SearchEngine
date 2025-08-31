package com.suyash.se.authserver.controller;

import com.suyash.se.authserver.dto.AuthenticationRequest;
import com.suyash.se.authserver.dto.AuthenticationResponse;
import com.suyash.se.authserver.dto.RefreshTokenRequest;
import com.suyash.se.authserver.service.AuthenticationService;
import com.suyash.se.authserver.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/api/auth/refresh")
    @ResponseBody
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        try {
            AuthenticationResponse response = authenticationService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/api/auth/me")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                var userDetails = authenticationService.getUserDetails(token);
                
                return ResponseEntity.ok(Map.of(
                    "username", userDetails.getUsername(),
                    "authorities", userDetails.getAuthorities(),
                    "enabled", userDetails.isEnabled()
                ));
            }
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody
    public ResponseEntity<Map<String, String>> logout() {
        // In a real implementation, you might want to blacklist the token
        // For now, just return success
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/api/auth/validate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                var userDetails = authenticationService.getUserDetails(token);
                
                if (jwtService.isTokenValid(token, userDetails)) {
                    return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "username", userDetails.getUsername(),
                        "authorities", userDetails.getAuthorities()
                    ));
                }
            }
            return ResponseEntity.ok(Map.of("valid", false));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}