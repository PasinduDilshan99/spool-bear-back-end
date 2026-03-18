package com.spoolbear.security.repository;

import com.spoolbear.security.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revokeAllForUser(Long userId);

    void revokeToken(String token);

    void deleteExpired(Instant now);
}
