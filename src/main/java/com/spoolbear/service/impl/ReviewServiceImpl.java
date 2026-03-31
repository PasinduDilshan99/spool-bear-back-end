package com.spoolbear.service.impl;

import com.spoolbear.exception.*;
import com.spoolbear.model.request.*;
import com.spoolbear.model.response.*;
import com.spoolbear.repository.ReviewRepository;
import com.spoolbear.service.CommonService;
import com.spoolbear.service.ReviewService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.ReviewValidationService;
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
    private final ReviewValidationService reviewValidationService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, CommonService commonService, ReviewValidationService reviewValidationService) {
        this.reviewRepository = reviewRepository;
        this.commonService = commonService;
        this.reviewValidationService = reviewValidationService;
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

    @Override
    public CommonResponse<InsertResponse> addReactToReview(ReviewReactRequest reviewReactRequest) {
        try {
            reviewValidationService.validateReviewReactRequest(reviewReactRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            ReviewAlreadyReactResponse reviewAlreadyReactResponse = reviewRepository.isReviewAlreadyReacted(reviewReactRequest.getReviewId(), userId);
            LOGGER.info("review already react response : {}", reviewAlreadyReactResponse);
            if (reviewAlreadyReactResponse == null) {
                reviewRepository.addReactToReview(reviewReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                        new InsertResponse("Successfully add Review react request"),
                        Instant.now()
                );
            } else if (reviewAlreadyReactResponse.getReactType().equalsIgnoreCase(reviewReactRequest.getReactType()) && reviewAlreadyReactResponse.getStatus() == 1) {
                reviewRepository.removeReactToReview(reviewReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_MESSAGE,
                        new InsertResponse("Successfully remove Review react request"),
                        Instant.now()
                );
            } else if (reviewAlreadyReactResponse.getReactType().equalsIgnoreCase(reviewReactRequest.getReactType()) && reviewAlreadyReactResponse.getStatus() != 1) {
                reviewRepository.chnageStatusReact(reviewReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_MESSAGE,
                        new InsertResponse("Successfully remove Review react request"),
                        Instant.now()
                );
            } else {
                reviewRepository.changeReactToReview(reviewReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                        new InsertResponse("Successfully change Review react request"),
                        Instant.now()
                );
            }

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert review reaction request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addReactToReviewComment(ReviewCommentReactRequest reviewCommentReactRequest) {
        try {
            reviewValidationService.validateReviewCommentReactRequest(reviewCommentReactRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            ReviewCommentAlreadyReactResponse reviewCommentAlreadyReactResponse =
                    reviewRepository.isReviewCommentAlreadyReacted(reviewCommentReactRequest.getCommentId(), userId);
            LOGGER.info("review comment already react response : {}", reviewCommentAlreadyReactResponse);
            if (reviewCommentAlreadyReactResponse == null) {
                reviewRepository.addReactToReviewComment(reviewCommentReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                        new InsertResponse("Successfully add review comment react request"),
                        Instant.now()
                );
            } else if (reviewCommentAlreadyReactResponse.getReactType().equalsIgnoreCase(reviewCommentReactRequest.getReactType()) &&
                    reviewCommentAlreadyReactResponse.getStatus() == 1) {
                LOGGER.info("remove review comment react request");
                reviewRepository.removeReactToReviewComment(reviewCommentReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_MESSAGE,
                        new InsertResponse("Successfully remove review comment react request"),
                        Instant.now()
                );
            } else if (reviewCommentAlreadyReactResponse.getReactType().equalsIgnoreCase(reviewCommentReactRequest.getReactType()) &&
                    reviewCommentAlreadyReactResponse.getStatus() != 1) {
                LOGGER.info("change review comment react request status");
                reviewRepository.chnageCommentStatusReact(reviewCommentReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_MESSAGE,
                        new InsertResponse("Successfully remove review comment react request"),
                        Instant.now()
                );
            } else {
                LOGGER.info("change review comment react request");
                reviewRepository.changeReactToReviewComment(reviewCommentReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                        new InsertResponse("Successfully change review comment react request"),
                        Instant.now()
                );
            }

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert review comment reaction request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addCommentToReview(ReviewCommentRequest reviewCommentRequest) {
        try {
            reviewValidationService.validateReviewCommentRequest(reviewCommentRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            reviewRepository.addCommentToReview(reviewCommentRequest, userId);
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    new InsertResponse("Successfully add comment to review"),
                    Instant.now());

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert review comment request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addReview(InsertReviewRequest insertReviewRequest) {
        try {
            reviewValidationService.validateInsertReviewRequest(insertReviewRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            Long reviewId = reviewRepository.addReviewDetails(insertReviewRequest, userId);
            reviewRepository.addReviewImages(insertReviewRequest.getImages(), reviewId, userId);
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    new InsertResponse("Successfully add review"),
                    Instant.now());

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert review request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<ReviewDetailsResponse> getReviewById(ReviewDetailsByIdRequest reviewDetailsByIdRequest) {
        LOGGER.info("Start fetching all reviews from repository");
        try {
            ReviewDetailsResponse reviewDetailsResponse = reviewRepository.getReviewById(reviewDetailsByIdRequest);

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    reviewDetailsResponse,
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
    public CommonResponse<List<ReviewDetailsResponse>> getReviewsByProductId(ReviewsForProductIdRequest reviewsForProductIdRequest) {
        LOGGER.info("Start fetching reviews by product id from repository");
        try {
            List<ReviewDetailsResponse> reviewDetailsResponses = reviewRepository.getReviewsByProductId(reviewsForProductIdRequest);

            if (reviewDetailsResponses.isEmpty()) {
                LOGGER.warn("No reviews for product id found in database");
                throw new DataNotFoundErrorExceptionHandler("No reviews found for product id");
            }

            LOGGER.info("Fetched {} reviews by product id successfully", reviewDetailsResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    reviewDetailsResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching reviews by product id : {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch reviews by product id from database");
        } finally {
            LOGGER.info("End fetching reviews by product id from repository");
        }
    }
}
