package com.suyash.se.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extract authorities from the JWT token
        List<String> authorities = jwt.getClaimAsStringList("authorities");
        
        if (authorities == null) {
            return List.of();
        }
        
        return authorities.stream()
                .map(authority -> {
                    // Convert ROLE_ADMIN to ADMIN for hasRole() to work
                    if (authority.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority(authority);
                    } else {
                        return new SimpleGrantedAuthority("ROLE_" + authority);
                    }
                })
                .collect(Collectors.toList());
    }
}