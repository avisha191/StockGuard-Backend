
package com.stockguard.dto;

public record AuthResponse(
    String token,
    String email,
    String role
) {}

