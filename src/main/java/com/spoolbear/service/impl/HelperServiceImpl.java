package com.spoolbear.service.impl;

import com.spoolbear.service.HelperService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class HelperServiceImpl implements HelperService {
    public String generateUniqueCode() {
        return UUID.randomUUID().toString();
    }


}
