package com.spoolbear.validations.impl;

import com.spoolbear.model.request.MaterialDetailsRequest;
import com.spoolbear.validations.CommonValidationService;
import com.spoolbear.validations.MaterialValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaterialValidationServiceImpl implements MaterialValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialValidationServiceImpl.class);

    private final CommonValidationService commonValidationService;

    @Autowired
    public MaterialValidationServiceImpl(CommonValidationService commonValidationService) {
        this.commonValidationService = commonValidationService;
    }

    @Override
    public void validateMaterialDetailsRequest(MaterialDetailsRequest materialDetailsRequest) {

    }
}
