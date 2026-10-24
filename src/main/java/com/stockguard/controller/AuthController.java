
package com.stockguard.controller;

import com.stockguard.dto.AuthResponse;
import com.stockguard.dto.LoginRequest;
import com.stockguard.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}


