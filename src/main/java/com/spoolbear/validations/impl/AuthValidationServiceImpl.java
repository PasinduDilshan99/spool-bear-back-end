package com.spoolbear.validations.impl;

import com.spoolbear.model.request.PasswordChangeRequest;
import com.spoolbear.model.request.ResetPasswordRequest;
import com.spoolbear.model.request.SecretQuestionsUpdateRequest;
import com.spoolbear.validations.AuthValidationService;
import com.spoolbear.validations.CommonValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthValidationServiceImpl implements AuthValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthValidationServiceImpl.class);

    private final CommonValidationService commonValidationService;

    @Autowired
    public AuthValidationServiceImpl(CommonValidationService commonValidationService) {
        this.commonValidationService = commonValidationService;
    }


    @Override
    public void validateResetPasswordRequest(ResetPasswordRequest resetPasswordRequest) {

    }

    @Override
    public void validatePasswordChangeRequest(PasswordChangeRequest passwordChangeRequest) {

    }

    @Override
    public void validateSecretQuestionsUpdateRequest(SecretQuestionsUpdateRequest secretQuestionsUpdateRequest) {

    }
}
