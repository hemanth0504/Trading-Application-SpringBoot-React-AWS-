package com.Hemanth.trading.controller;

import com.Hemanth.trading.config.JwtProvider;
import com.Hemanth.trading.modal.User;
import com.Hemanth.trading.repository.UserRepository;
import com.Hemanth.trading.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(
            @RequestBody User user) throws Exception {

        String email = user.getEmail();
        String password = user.getPassword();
        String fullName = user.getFullName();
        String mobile=user.getMobile();


        User isEmailExist = userRepository.findByEmail(email);

        if (isEmailExist!=null) {

            throw new Exception("Email Is Already Used With Another Account");
        }

        // Create new user
        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setMobile(mobile);


        User savedUser = userRepository.save(createdUser);


        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

    }






}
