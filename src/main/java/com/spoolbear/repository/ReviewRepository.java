package com.spoolbear.repository;

import com.spoolbear.model.request.InsertReviewRequest;
import com.spoolbear.model.request.ReviewCommentReactRequest;
import com.spoolbear.model.request.ReviewCommentRequest;
import com.spoolbear.model.request.ReviewReactRequest;
import com.spoolbear.model.response.ReviewAlreadyReactResponse;
import com.spoolbear.model.response.ReviewCommentAlreadyReactResponse;
import com.spoolbear.model.response.ReviewDetailsResponse;

import java.util.List;

public interface ReviewRepository {
    List<ReviewDetailsResponse> getAllReviews();

    List<ReviewDetailsResponse> getReviewsByUser(Long userId);

    ReviewAlreadyReactResponse isReviewAlreadyReacted(Long reviewId, Long userId);

    void addReactToReview(ReviewReactRequest reviewReactRequest, Long userId);

    void removeReactToReview(ReviewReactRequest reviewReactRequest, Long userId);

    void changeReactToReview(ReviewReactRequest reviewReactRequest, Long userId);

    void addCommentToReview(ReviewCommentRequest reviewCommentRequest, Long userId);

    ReviewCommentAlreadyReactResponse isReviewCommentAlreadyReacted(Long commentId, Long userId);

    void addReactToReviewComment(ReviewCommentReactRequest reviewCommentReactRequest, Long userId);

    void removeReactToReviewComment(ReviewCommentReactRequest reviewCommentReactRequest, Long userId);

    void changeReactToReviewComment(ReviewCommentReactRequest reviewCommentReactRequest, Long userId);

    Long addReviewDetails(InsertReviewRequest insertReviewRequest, Long userId);

    void addReviewImages(List<InsertReviewRequest.ReviewImage> images, Long reviewId, Long userId);
}
