package com.nexbank.authservice.service;

import com.nexbank.authservice.domain.dto.RefreshToken;
import com.nexbank.authservice.entrypoint.exception.BusinessException;
import com.nexbank.authservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        return repository.save(token);
    }

    @Transactional(readOnly = true)
    public RefreshToken verify(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (refreshToken.isRevoked() ||
                refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token expired or revoked");
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }

    @Transactional
    public void revokeAllByUser(UUID userId) {
        repository.deleteByUserId(userId);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        repository.deleteAllExpired(Instant.now());
    }
}
