package com.suyash.se.authserver.config;

import com.suyash.se.authserver.entity.Role;
import com.suyash.se.authserver.entity.User;
import com.suyash.se.authserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@searchengine.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(Role.ADMIN));
            admin.setEnabled(true);
            
            userRepository.save(admin);
            log.info("Default admin user created: username=admin, password=admin123");
        }
        
        // Create default regular user if not exists
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@searchengine.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(Role.USER));
            user.setEnabled(true);
            
            userRepository.save(user);
            log.info("Default user created: username=user, password=user123");
        }
    }
}