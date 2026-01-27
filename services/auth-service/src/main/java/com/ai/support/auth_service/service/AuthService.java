package com.ai.support.auth_service.service;

import com.ai.support.auth_service.domain.Role;
import com.ai.support.auth_service.domain.User;
import com.ai.support.auth_service.dto.AuthResponse;
import com.ai.support.auth_service.dto.LoginRequest;
import com.ai.support.auth_service.dto.RegisterRequest;
import com.ai.support.auth_service.exception.BadRequestException;
import com.ai.support.auth_service.exception.UnauthorizedException;
import com.ai.support.auth_service.repository.UserRepository;
import com.ai.support.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return new AuthResponse(jwtService.generateToken(user));
    }
}