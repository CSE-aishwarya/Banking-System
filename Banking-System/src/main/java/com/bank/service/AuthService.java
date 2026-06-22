package com.bank.service;

import com.bank.dto.AuthResponse;

import com.bank.dto.LoginRequest;
import com.bank.dto.RegisterRequest;
import com.bank.model.User;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bank.dto.PasswordResetRequest;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private EmailService emailService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        UserDetails userDetails = userDetailsService
            .loadUserByUsername(request.getEmail());
        String token =
            jwtService.generateToken(userDetails);
        User user = userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() ->
                new RuntimeException("User not found"));
        return new AuthResponse(
            token,
            user.getEmail(),
            user.getFullName(),
            user.getRole().name()
        );
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(
                request.getEmail())) {
            throw new RuntimeException(
                "Email already exists!");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(
            request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(User.Role.CUSTOMER);
        userRepository.save(user);

        // Send welcome email
        emailService.sendWelcomeEmail(
            request.getEmail(),
            request.getFullName()
        );
        

        return "User registered successfully!";
    }
    public String resetPassword(
            PasswordResetRequest request) {

        // Check if email exists
        User user = userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() ->
                new RuntimeException(
                    "Email not found! Please check " +
                    "your registered email."));

        // Check passwords match
        if (!request.getNewPassword().equals(
                request.getConfirmPassword())) {
            throw new RuntimeException(
                "Passwords do not match!");
        }

        // Check password length
        if (request.getNewPassword().length() < 6) {
            throw new RuntimeException(
                "Password must be at least 6 characters!");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(
            request.getNewPassword()));
        userRepository.save(user);

        // Log email notification
        emailService.sendPasswordResetEmail(
            user.getEmail(),
            user.getFullName()
        );

        return "Password reset successfully!";
    }
}