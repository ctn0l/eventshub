package com.example.eventshub.controller;

import com.example.eventshub.dto.LoginRequest;
import com.example.eventshub.dto.TokenResponse;
import com.example.eventshub.model.User;
import com.example.eventshub.model.enums.UserRole;
import com.example.eventshub.repository.UserRepository;
import com.example.eventshub.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates user credentials and returns a JWT token.
     * @param request login payload with email and password
     * @return TokenResponse containing the signed JWT
     * @throws org.springframework.security.authentication.BadCredentialsException on invalid credentials
     * Note: Token embeds username and roles.
     */
    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        // Autentica le credenziali con AuthenticationManager
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserDetails principal = (UserDetails) auth.getPrincipal();

        // Genera token firmato
        String token = jwtUtil.generateToken(principal);
        return new TokenResponse(token);
    }

    /**
     * Registers a new user with encoded password.
     * @param request login payload reused for simplicity (email+password)
     * @return TokenResponse with immediate JWT for the new user
     * @throws IllegalArgumentException if email already exists
     * Note: Assigns default role USER.
     */
    @PostMapping("/register")
    public TokenResponse register(@RequestBody @Valid LoginRequest request) {
        // Valida email
        if (userRepository.existsByEmail(request.username())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        userRepository.save(user);

        // Autenticazione immediata e token
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
        String token = jwtUtil.generateToken(principal);
        return new TokenResponse(token);
    }
}
