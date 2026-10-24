
package com.stockguard.service;

import com.stockguard.dto.AuthResponse;
import com.stockguard.dto.LoginRequest;
import com.stockguard.entity.AdminUser;
import com.stockguard.repository.AdminUserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {

        // Normalize the email to avoid case-sensitive login issues.
        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Find the existing admin account.
        AdminUser user = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        // Verify the entered password against its BCrypt hash.
        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        // Generate a JWT after successful authentication.
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        // Return the token and authenticated user's details.
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole()
        );
    }
}



