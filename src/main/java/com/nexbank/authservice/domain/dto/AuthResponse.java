package com.nexbank.authservice.domain.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}