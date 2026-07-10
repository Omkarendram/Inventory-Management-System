package com.inventory.config;

import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserDataInitializer {

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            upsertUser(userRepository, passwordEncoder, "ADMIN001", "Admin", "admin123", "ADMIN");
            upsertUser(userRepository, passwordEncoder, "USER001", "Rishu", "user123", "USER");
        };
    }

    private void upsertUser(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        String userId,
        String username,
        String password,
        String role
    ) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            user = userRepository.findByUsername(username);
        }
        if (user == null) {
            user = new User();
        }

        user.setUserId(userId);
        user.setUsername(username);
        if (user.getPhone() == null) {
            user.setPhone("");
        }
        if (user.getEmail() == null) {
            user.setEmail("");
        }
        if (user.getAddress() == null) {
            user.setAddress("");
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
    }
}
