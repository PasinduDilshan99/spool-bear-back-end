package com.printing._d.service.impl;

import com.printing._d.service.HelperService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class HelperServiceImpl implements HelperService {
    public String generateUniqueCode() {
        return UUID.randomUUID().toString();
    }


}
