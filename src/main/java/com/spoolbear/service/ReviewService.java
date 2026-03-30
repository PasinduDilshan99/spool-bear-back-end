package com.spoolbear.service;

import com.spoolbear.model.request.InsertReviewRequest;
import com.spoolbear.model.request.ReviewCommentReactRequest;
import com.spoolbear.model.request.ReviewCommentRequest;
import com.spoolbear.model.request.ReviewReactRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.ReviewDetailsResponse;

import java.util.List;

public interface ReviewService {
    CommonResponse<List<ReviewDetailsResponse>> getAllReviews();

    CommonResponse<List<ReviewDetailsResponse>> getReviewsByUser();

    CommonResponse<InsertResponse> addReactToReview(ReviewReactRequest reviewReactRequest);

    CommonResponse<InsertResponse> addReactToReviewComment(ReviewCommentReactRequest reviewCommentReactRequest);

    CommonResponse<InsertResponse> addCommentToReview(ReviewCommentRequest reviewCommentRequest);

    CommonResponse<InsertResponse> addReview(InsertReviewRequest insertReviewRequest);
}
