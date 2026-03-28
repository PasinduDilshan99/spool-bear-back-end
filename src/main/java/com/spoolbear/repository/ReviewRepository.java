package com.spoolbear.repository;

import com.spoolbear.model.response.ReviewDetailsResponse;

import java.util.List;

public interface ReviewRepository {
    List<ReviewDetailsResponse> getAllReviews();

    List<ReviewDetailsResponse> getReviewsByUser(Long userId);
}
