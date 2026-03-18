package com.spoolbear.security.service;

import com.spoolbear.security.model.RefreshToken;
import com.spoolbear.security.model.User;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, String token, Instant expiryDate);

    Optional<RefreshToken> validateRefreshToken(String token);

    void revokeToken(String token);
}
