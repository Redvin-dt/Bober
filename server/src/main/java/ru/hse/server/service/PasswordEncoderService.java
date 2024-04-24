package ru.hse.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderService {
    @Value("${userService.passwordSecret}")
    private String passwordSecret;
    @Value("${userService.hashIteration}")
    private int iteration;
    @Value("${userService.saltLength}")
    private int saltLength;
    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder;

    PasswordEncoderService() {
        pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder(passwordSecret,
                saltLength, iteration,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        pbkdf2PasswordEncoder.setEncodeHashAsBase64(true);
    }

    public String hashPassword(String plainPassword) {
        return pbkdf2PasswordEncoder.encode(plainPassword);
    }

    public boolean matchPassword(String plainPassword, String encodedPassword) {
        return pbkdf2PasswordEncoder.matches(plainPassword, encodedPassword);
    }
}


