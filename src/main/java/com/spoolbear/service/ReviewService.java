package com.spoolbear.service;

import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.ReviewDetailsResponse;

import java.util.List;

public interface ReviewService {
    CommonResponse<List<ReviewDetailsResponse>> getAllReviews();

    CommonResponse<List<ReviewDetailsResponse>> getReviewsByUser();
}
