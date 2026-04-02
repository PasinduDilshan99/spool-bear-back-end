package com.spoolbear.service.impl;

import com.spoolbear.exception.UnAuthenticateErrorExceptionHandler;
import com.spoolbear.repository.CommonRepository;
import com.spoolbear.security.model.CustomUserDetails;
import com.spoolbear.security.model.User;
import com.spoolbear.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CommonServiceImpl implements CommonService {


    private static final Logger LOGGER = LoggerFactory.getLogger(CommonServiceImpl.class);

    private final CommonRepository commonRepository;

    @Autowired
    public CommonServiceImpl(CommonRepository commonRepository) {
        this.commonRepository = commonRepository;
    }

    @Value("${otp.generate.length}")
    private int otpGeneratedLength;

    @Override
    public Long getUserIdBySecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UnAuthenticateErrorExceptionHandler("No authenticated user");
        }
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.getDomainUser();
        return user.getId();
    }

    @Override
    public Long getUserIdBySecurityContextWithOutException() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = principal.getDomainUser();
        return user.getId();
    }

    @Override
    public String generateRandomOtp() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < otpGeneratedLength; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

}
