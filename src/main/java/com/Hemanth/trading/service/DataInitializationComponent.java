package com.Hemanth.trading.service;

import com.Hemanth.trading.domain.USER_ROLE;
import com.Hemanth.trading.model.User;
import com.Hemanth.trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;


    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializationComponent(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder
                                       ) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;

    }

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        String adminUsername = "codewithHemanth.trading@gmail.com";

        if (userRepository.findByEmail(adminUsername)==null) {
            User adminUser = new User();

            adminUser.setPassword(passwordEncoder.encode("codewithHemanth.trading"));
            adminUser.setFullName("Code With Hemanth.trading");
            adminUser.setEmail(adminUsername);
            adminUser.setRole(USER_ROLE.ROLE_ADMIN);
            User admin=userRepository.save(adminUser);
        }
    }

}
