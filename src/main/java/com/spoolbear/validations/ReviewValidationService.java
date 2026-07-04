package com.spoolbear.validations;

import com.spoolbear.model.request.InsertReviewRequest;
import com.spoolbear.model.request.ReviewCommentReactRequest;
import com.spoolbear.model.request.ReviewCommentRequest;
import com.spoolbear.model.request.ReviewReactRequest;

public interface ReviewValidationService {
    void validateReviewReactRequest(ReviewReactRequest reviewReactRequest);


    void validateReviewCommentRequest(ReviewCommentRequest reviewCommentRequest);

    void validateReviewCommentReactRequest(ReviewCommentReactRequest reviewCommentReactRequest);

    void validateInsertReviewRequest(InsertReviewRequest insertReviewRequest);
}
