package com.spoolbear.validations.impl;

import com.spoolbear.model.request.DesignOrderInsertRequest;
import com.spoolbear.model.request.PrintingOrderInsertRequest;
import com.spoolbear.model.request.ProductOrderInsertRequest;
import com.spoolbear.validations.CommonValidationService;
import com.spoolbear.validations.OrderValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderValidationServiceImpl implements OrderValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderValidationServiceImpl.class);

    private final CommonValidationService commonValidationService;

    @Autowired
    public OrderValidationServiceImpl(CommonValidationService commonValidationService) {
        this.commonValidationService = commonValidationService;
    }

    @Override
    public void validatePrintingOrderRequest(PrintingOrderInsertRequest printingOrderInsertRequest) {

    }

    @Override
    public void validateDesignOrderInsertRequest(DesignOrderInsertRequest designOrderInsertRequest) {

    }

    @Override
    public void validateProductOrderInsertRequest(ProductOrderInsertRequest productOrderInsertRequest) {

    }
}
