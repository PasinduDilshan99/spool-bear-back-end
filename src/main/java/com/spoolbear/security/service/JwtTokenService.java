package com.spoolbear.security.service;

import com.spoolbear.security.model.InsertJwtTokenRequest;

public interface JwtTokenService {
    void insertJwtToken(InsertJwtTokenRequest insertJwtTokenRequest);
}
