package com.spoolbear.validations.impl;

import com.spoolbear.model.request.WishListInsertRequest;
import com.spoolbear.validations.CommonValidationService;
import com.spoolbear.validations.WishListValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishListValidationServiceImpl implements WishListValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishListValidationServiceImpl.class);

    private final CommonValidationService commonValidationService;

    @Autowired
    public WishListValidationServiceImpl(CommonValidationService commonValidationService) {
        this.commonValidationService = commonValidationService;
    }

    @Override
    public void validateWishListInsertRequest(WishListInsertRequest wishListInsertRequest) {

    }
}
