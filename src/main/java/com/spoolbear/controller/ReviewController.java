package com.spoolbear.controller;

import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.ReviewDetailsResponse;
import com.spoolbear.service.ReviewService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(path = "/reviews-by-user")
    public ResponseEntity<CommonResponse<List<ReviewDetailsResponse>>> getReviewsByUser() {
        LOGGER.info("{} Start execute get reviews by user {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<ReviewDetailsResponse>> response = reviewService.getReviewsByUser();
        LOGGER.info("{} End execute get reviews by user {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
