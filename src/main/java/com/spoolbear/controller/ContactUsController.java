package com.spoolbear.controller;

import com.spoolbear.model.request.InsertInquiryRequest;
import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.ProductsCartResponse;
import com.spoolbear.service.ContactUsService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/contact-us")
public class ContactUsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsController.class);

    private final ContactUsService contactUsService;

    @Autowired
    public ContactUsController(ContactUsService contactUsService) {
        this.contactUsService = contactUsService;
    }

    @PostMapping(path = "/inquiry")
    public ResponseEntity<CommonResponse<InsertResponse>> addInquiry(@RequestBody InsertInquiryRequest insertInquiryRequest) {
        LOGGER.info("{} Start execute add inquiry request {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = contactUsService.addInquiry(insertInquiryRequest);
        LOGGER.info("{} End execute add inquiry request {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
