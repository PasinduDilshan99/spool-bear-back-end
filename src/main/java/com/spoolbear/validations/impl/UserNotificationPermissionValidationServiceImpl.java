package com.spoolbear.validations.impl;

import com.spoolbear.exception.ValidationFailedErrorExceptionHandler;
import com.spoolbear.model.request.UpdateUserNotificationPermissionRequest;
import com.spoolbear.model.response.ValidationFailedResponse;
import com.spoolbear.model.response.ValidationResultResponse;
import com.spoolbear.validations.CommonValidationService;
import com.spoolbear.validations.UserNotificationPermissionValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserNotificationPermissionValidationServiceImpl implements UserNotificationPermissionValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationPermissionValidationServiceImpl.class);

    private final CommonValidationService commonValidationService;

    @Autowired
    public UserNotificationPermissionValidationServiceImpl(CommonValidationService commonValidationService) {
        this.commonValidationService = commonValidationService;
    }

    @Override
    public void validateUpdateUserNotificationPermissionRequest(UpdateUserNotificationPermissionRequest updateUserNotificationPermissionRequest) {
        List<ValidationFailedResponse> validationFailedResponses = new ArrayList<>();
        ValidationResultResponse name = commonValidationService.validateNotNullOrEmpty("name", updateUserNotificationPermissionRequest.getName());
        if (!name.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(name.getField()).value(name.getMessage()).build());
        }
        ValidationResultResponse value = commonValidationService.validateBoolean("value", updateUserNotificationPermissionRequest.getValue());
        if (!value.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(value.getField()).value(value.getMessage()).build());
        }
        LOGGER.info(validationFailedResponses.toString());
        if (!validationFailedResponses.isEmpty()) {
            throw new ValidationFailedErrorExceptionHandler("Validation failed : updateUserNotificationPermissionRequest", validationFailedResponses);
        }
    }

}
