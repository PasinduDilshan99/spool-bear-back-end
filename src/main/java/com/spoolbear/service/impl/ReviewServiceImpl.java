package com.spoolbear.service.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.ReviewDetailsResponse;
import com.spoolbear.repository.ReviewRepository;
import com.spoolbear.service.CommonService;
import com.spoolbear.service.ReviewService;
import com.spoolbear.util.CommonResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final CommonService commonService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, CommonService commonService) {
        this.reviewRepository = reviewRepository;
        this.commonService = commonService;
    }

    @Override
    public CommonResponse<List<ReviewDetailsResponse>> getAllReviews() {
        LOGGER.info("Start fetching all reviews from repository");
        try {
            List<ReviewDetailsResponse> reviewDetailsResponses = reviewRepository.getAllReviews();

            if (reviewDetailsResponses.isEmpty()) {
                LOGGER.warn("No reviews found in database");
                throw new DataNotFoundErrorExceptionHandler("No reviews found");
            }

            LOGGER.info("Fetched {} reviews successfully", reviewDetailsResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    reviewDetailsResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching reviews: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch reviews from database");
        } finally {
            LOGGER.info("End fetching reviews from repository");
        }
    }

    @Override
    public CommonResponse<List<ReviewDetailsResponse>> getReviewsByUser() {
        LOGGER.info("Start fetching reviews by user from repository");
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            List<ReviewDetailsResponse> reviewDetailsResponses = reviewRepository.getReviewsByUser(userId);

            if (reviewDetailsResponses.isEmpty()) {
                LOGGER.warn("No reviews for user id found in database");
                throw new DataNotFoundErrorExceptionHandler("No reviews found");
            }

            LOGGER.info("Fetched {} reviews by user successfully", reviewDetailsResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    reviewDetailsResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching reviews by user : {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch reviews by user from database");
        } finally {
            LOGGER.info("End fetching reviews by user from repository");
        }
    }
}
