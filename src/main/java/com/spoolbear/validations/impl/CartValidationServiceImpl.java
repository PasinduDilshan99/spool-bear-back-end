package com.spoolbear.validations.impl;

import com.spoolbear.model.request.FetchCartRequest;
import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.request.RemoveAllFromCartRequest;
import com.spoolbear.model.request.RemoveItemFromCartRequest;
import com.spoolbear.validations.CartValidationService;
import com.spoolbear.validations.CommonValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartValidationServiceImpl implements CartValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartValidationServiceImpl.class);

    private final CommonValidationService commonValidationService;

    @Autowired
    public CartValidationServiceImpl(CommonValidationService commonValidationService) {
        this.commonValidationService = commonValidationService;
    }

    @Override
    public void validateInsertItemToCartRequest(InsertItemToCartRequest insertItemToCartRequest) {

    }

    @Override
    public void validateFetchCartRequest(FetchCartRequest fetchCartRequest) {

    }

    @Override
    public void validateRemoveItemFromCartRequest(RemoveItemFromCartRequest removeItemFromCartRequest) {

    }

    @Override
    public void validateRemoveAllFromCartRequest(RemoveAllFromCartRequest removeAllFromCartRequest) {

    }
}
