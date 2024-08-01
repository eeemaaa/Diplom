package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.findByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("password")); // Используйте более безопасный пароль
            user.setEmail("user@example.com");
            userRepository.save(user);
        }
    }
}
