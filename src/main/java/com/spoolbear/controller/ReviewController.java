package com.spoolbear.controller;

import com.spoolbear.model.request.*;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.ReviewDetailsResponse;
import com.spoolbear.service.ReviewService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/reviews")
public class ReviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<CommonResponse<List<ReviewDetailsResponse>>> getAllReviews() {
        LOGGER.info("{} Start execute get all reviews {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<ReviewDetailsResponse>> response = reviewService.getAllReviews();
        LOGGER.info("{} End execute get all reviews {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/review-by-id")
    public ResponseEntity<CommonResponse<ReviewDetailsResponse>> getReviewById(@RequestBody ReviewDetailsByIdRequest reviewDetailsByIdRequest) {
        LOGGER.info("{} Start execute get all reviews {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<ReviewDetailsResponse> response = reviewService.getReviewById(reviewDetailsByIdRequest);
        LOGGER.info("{} End execute get all reviews {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/reviews-by-product-id")
    public ResponseEntity<CommonResponse<List<ReviewDetailsResponse>>> getReviewsByProductId(@RequestBody ReviewsForProductIdRequest reviewsForProductIdRequest) {
        LOGGER.info("{} Start execute get reviews by product id {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<ReviewDetailsResponse>> response = reviewService.getReviewsByProductId(reviewsForProductIdRequest);
        LOGGER.info("{} End execute get reviews by product id {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/reviews-by-user")
    public ResponseEntity<CommonResponse<List<ReviewDetailsResponse>>> getReviewsByUser() {
        LOGGER.info("{} Start execute get reviews by user {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<ReviewDetailsResponse>> response = reviewService.getReviewsByUser();
        LOGGER.info("{} End execute get reviews by user {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-review")
    public ResponseEntity<CommonResponse<InsertResponse>> addReview(@RequestBody InsertReviewRequest insertReviewRequest) {
        LOGGER.info("{} Start execute add review {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = reviewService.addReview(insertReviewRequest);
        LOGGER.info("{} End execute add review {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-react")
    public ResponseEntity<CommonResponse<InsertResponse>> addReactToReview(@RequestBody ReviewReactRequest reviewReactRequest) {
        LOGGER.info("{} Start execute add react to review {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = reviewService.addReactToReview(reviewReactRequest);
        LOGGER.info("{} End execute add react to review {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-comment-react")
    public ResponseEntity<CommonResponse<InsertResponse>> addReactToReviewComment(@RequestBody ReviewCommentReactRequest reviewCommentReactRequest) {
        LOGGER.info("{} Start execute add react to review comment {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = reviewService.addReactToReviewComment(reviewCommentReactRequest);
        LOGGER.info("{} End execute add react to review comment {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-comment")
    public ResponseEntity<CommonResponse<InsertResponse>> addCommentToReview(@RequestBody ReviewCommentRequest reviewCommentRequest) {
        LOGGER.info("{} Start execute add comment to review {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = reviewService.addCommentToReview(reviewCommentRequest);
        LOGGER.info("{} End execute add comment to review {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
