package com.spoolbear.security.repository;

import com.spoolbear.security.model.InsertJwtTokenRequest;

public interface JwtTokenRepository {
    void insertJwtToken(InsertJwtTokenRequest request);
}
