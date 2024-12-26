package com.example.Tata.Serivicelayer;

import com.example.Tata.Domain.User;
import com.example.Tata.Repository.UserRepository;
import com.example.Tata.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;  // Ensure passwordEncoder is injected
    }
    public String generateToken(String email) {
        return jwtUtil.generateToken(email);  // Use jwtUtil to generate token
    }

    public boolean validateToken(String token, String email) {
        return jwtUtil.validateToken(token, email);  // Use jwtUtil to validate token
    }


    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String registerUser(String firstName, String lastName, String email, String phoneNumber, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
        return "User registered successfully!";
    }

    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    public String validateToken(String token) {
        String email = jwtUtil.extractEmail(token);
        if (email != null && jwtUtil.validateToken(token, email)) {
            return "Token is valid for email: " + email;
        }
        return "Invalid or expired token";
    }
}
