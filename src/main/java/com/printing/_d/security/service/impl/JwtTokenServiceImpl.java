package com.printing._d.security.service.impl;

import com.printing._d.security.model.InsertJwtTokenRequest;
import com.printing._d.security.repository.JwtTokenRepository;
import com.printing._d.security.service.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenServiceImpl.class);

    private final JwtTokenRepository jwtTokenRepository;

    @Autowired
    public JwtTokenServiceImpl(JwtTokenRepository jwtTokenRepository) {
        this.jwtTokenRepository = jwtTokenRepository;
    }

    @Override
    public void insertJwtToken(InsertJwtTokenRequest request) {
        jwtTokenRepository.insertJwtToken(request);
    }

}
