package com.nexbank.authservice.entrypoint.exception;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String correlationId,
        LocalDateTime timestamp
) {}