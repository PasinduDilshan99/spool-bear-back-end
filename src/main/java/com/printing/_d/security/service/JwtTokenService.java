package com.printing._d.security.service;

import com.printing._d.security.model.InsertJwtTokenRequest;

public interface JwtTokenService {
    void insertJwtToken(InsertJwtTokenRequest insertJwtTokenRequest);
}
