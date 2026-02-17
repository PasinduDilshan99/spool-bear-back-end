package com.printing._d.security.repository;

import com.printing._d.security.model.InsertJwtTokenRequest;

public interface JwtTokenRepository {
    void insertJwtToken(InsertJwtTokenRequest request);
}
