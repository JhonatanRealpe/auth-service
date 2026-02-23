package com.nexbank.authservice.domain.dto;

public record LoginRequest(
        String email,
        String password
) {}
