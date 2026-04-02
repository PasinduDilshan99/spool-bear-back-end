package com.spoolbear.validations.impl;

import com.spoolbear.model.request.InsertReviewRequest;
import com.spoolbear.model.request.ReviewCommentReactRequest;
import com.spoolbear.model.request.ReviewCommentRequest;
import com.spoolbear.model.request.ReviewReactRequest;
import com.spoolbear.validations.CommonValidationService;
import com.spoolbear.validations.ReviewValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewValidationServiceImpl implements ReviewValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewValidationServiceImpl.class);

    private final CommonValidationService commonValidationService;

    @Autowired
    public ReviewValidationServiceImpl(CommonValidationService commonValidationService) {
        this.commonValidationService = commonValidationService;
    }

    @Override
    public void validateReviewReactRequest(ReviewReactRequest reviewReactRequest) {

    }

    @Override
    public void validateReviewCommentRequest(ReviewCommentRequest reviewCommentRequest) {

    }

    @Override
    public void validateReviewCommentReactRequest(ReviewCommentReactRequest reviewCommentReactRequest) {

    }

    @Override
    public void validateInsertReviewRequest(InsertReviewRequest insertReviewRequest) {

    }
}
