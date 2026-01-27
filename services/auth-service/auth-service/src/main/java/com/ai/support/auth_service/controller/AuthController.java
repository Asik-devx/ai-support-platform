package com.ai.support.auth_service.controller;

import com.ai.support.auth_service.dto.AuthResponse;
import com.ai.support.auth_service.dto.LoginRequest;
import com.ai.support.auth_service.dto.RegisterRequest;
import com.ai.support.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public Object me(Authentication authentication) {
        return authentication.getPrincipal();
    }
}

