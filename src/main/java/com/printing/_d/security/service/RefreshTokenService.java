package com.printing._d.security.service;

import com.printing._d.security.model.RefreshToken;
import com.printing._d.security.model.User;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, String token, Instant expiryDate);

    Optional<RefreshToken> validateRefreshToken(String token);

    void revokeToken(String token);
}
