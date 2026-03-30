package com.spoolbear.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.InsertReviewRequest;
import com.spoolbear.model.request.ReviewCommentReactRequest;
import com.spoolbear.model.request.ReviewCommentRequest;
import com.spoolbear.model.request.ReviewReactRequest;
import com.spoolbear.model.response.*;
import com.spoolbear.queries.BlogQueries;
import com.spoolbear.queries.ReviewQueries;
import com.spoolbear.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReviewDetailsResponse> getAllReviews() {
        String GET_REVIEW_DETAILS = ReviewQueries.GET_REVIEW_DETAILS;
        try {
            LOGGER.info("Executing query to fetch all activity review details.");

            Map<Long, ReviewDetailsResponse> reviewMap = new LinkedHashMap<>();

            jdbcTemplate.query(GET_REVIEW_DETAILS, rs -> {
                Long reviewId = rs.getLong("review_id");
                ReviewDetailsResponse review = reviewMap.get(reviewId);

                if (review == null) {
                    review = ReviewDetailsResponse.builder()
                            .reviewId(reviewId)
                            .orderId(rs.getLong("order_id"))
                            .orderType(rs.getString("order_type"))
                            .productId(rs.getLong("product_id"))
                            .productName(rs.getString("product_name"))
                            .reviewComment(rs.getString("comment"))
                            .rating(rs.getBigDecimal("rating"))
                            .reviewStatus(rs.getString("review_status"))
                            .reviewCreatedBy(rs.getLong("review_created_by"))
                            .reviewCreatedAt(rs.getTimestamp("review_created_at").toLocalDateTime())
                            .reviewUpdatedBy(rs.getObject("review_updated_by") != null ? rs.getLong("review_updated_by") : null)
                            .reviewUpdatedAt(rs.getTimestamp("review_updated_at") != null ? rs.getTimestamp("review_updated_at").toLocalDateTime() : null)
                            .images(new ArrayList<>())
                            .reactions(new ArrayList<>())
                            .comments(new ArrayList<>())
                            .build();
                    reviewMap.put(reviewId, review);
                }

                // Process Review Image
                Long imageId = rs.getObject("image_id", Long.class);
                if (imageId != null && review.getImages().stream().noneMatch(i -> i.getImageId().equals(imageId))) {
                    review.getImages().add(ReviewDetailsResponse.ReviewImage.builder()
                            .imageId(imageId)
                            .imageUrl(rs.getString("image_url"))
                            .imageCreatedBy(rs.getLong("image_created_by"))
                            .imageCreatedAt(rs.getTimestamp("image_created_at").toLocalDateTime())
                            .build());
                }

                // Process Review Reaction
                Long reviewReactionId = rs.getObject("review_reaction_id", Long.class);
                if (reviewReactionId != null && review.getReactions().stream().noneMatch(r -> r.getReviewReactionId().equals(reviewReactionId))) {
                    review.getReactions().add(ReviewDetailsResponse.ReviewReaction.builder()
                            .reviewReactionId(reviewReactionId)
                            .reactionReviewId(rs.getLong("reaction_review_id"))
                            .userId(rs.getLong("reaction_user_id"))
                            .userName(rs.getString("reaction_user_name"))
                            .reactionType(rs.getString("reaction_type"))
                            .reviewReactionStatus(rs.getString("review_reaction_status"))
                            .reactionCreatedAt(rs.getTimestamp("reaction_created_at").toLocalDateTime())
                            .build());
                }

                // Process Comment
                Long commentId = rs.getObject("comment_id", Long.class);
                ReviewDetailsResponse.Comment comment = null;
                if (commentId != null) {
                    comment = review.getComments().stream()
                            .filter(c -> c.getCommentId().equals(commentId))
                            .findFirst()
                            .orElse(null);
                    if (comment == null) {
                        comment = ReviewDetailsResponse.Comment.builder()
                                .commentId(commentId)
                                .commentReviewId(rs.getLong("comment_review_id"))
                                .userId(rs.getLong("comment_user_id"))
                                .userName(rs.getString("comment_user_name"))
                                .parentCommentId(rs.getObject("parent_comment_id") != null ? rs.getLong("parent_comment_id") : null)
                                .comment(rs.getString("comment"))
                                .commentStatus(rs.getString("comment_status"))
                                .commentCreatedAt(rs.getTimestamp("comment_created_at").toLocalDateTime())
                                .commentCreatedBy(rs.getLong("comment_created_by"))
                                .commentReactions(new ArrayList<>())
                                .build();
                        review.getComments().add(comment);
                    }
                }

                // Process Comment Reaction
                if (comment != null) {
                    Long commentReactionId = rs.getObject("comment_reaction_id", Long.class);
                    if (commentReactionId != null && comment.getCommentReactions().stream().noneMatch(cr -> cr.getCommentReactionId().equals(commentReactionId))) {
                        comment.getCommentReactions().add(ReviewDetailsResponse.Comment.CommentReaction.builder()
                                .commentReactionId(commentReactionId)
                                .commentReactionCommentId(rs.getLong("comment_reaction_comment_id"))
                                .userId(rs.getLong("comment_reaction_user_id"))
                                .userName(rs.getString("comment_reaction_user_name"))
                                .commentReactionType(rs.getString("comment_reaction_type"))
                                .commentReactionStatus(rs.getString("comment_reaction_status"))
                                .commentReactionCreatedBy(rs.getLong("comment_reaction_created_by"))
                                .commentReactionCreatedAt(rs.getTimestamp("comment_reaction_created_at").toLocalDateTime())
                                .build());
                    }
                }
            });

            return new ArrayList<>(reviewMap.values());

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching activity reviews: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch activity reviews from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching activity reviews: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching activity reviews");
        }
    }

    @Override
    public List<ReviewDetailsResponse> getReviewsByUser(Long userId) {
        String GET_REVIEW_DETAILS_BY_ID = ReviewQueries.GET_REVIEW_DETAILS_BY_ID;
        try {
            LOGGER.info("Executing query to fetch reviews by user.");

            Map<Long, ReviewDetailsResponse> reviewMap = new LinkedHashMap<>();

            jdbcTemplate.query(GET_REVIEW_DETAILS_BY_ID, new Object[]{userId},rs -> {
                Long reviewId = rs.getLong("review_id");
                ReviewDetailsResponse review = reviewMap.get(reviewId);

                if (review == null) {
                    review = ReviewDetailsResponse.builder()
                            .reviewId(reviewId)
                            .orderId(rs.getLong("order_id"))
                            .orderType(rs.getString("order_type"))
                            .productId(rs.getLong("product_id"))
                            .productName(rs.getString("product_name"))
                            .reviewComment(rs.getString("comment"))
                            .rating(rs.getBigDecimal("rating"))
                            .reviewStatus(rs.getString("review_status"))
                            .reviewCreatedBy(rs.getLong("review_created_by"))
                            .reviewCreatedAt(rs.getTimestamp("review_created_at").toLocalDateTime())
                            .reviewUpdatedBy(rs.getObject("review_updated_by") != null ? rs.getLong("review_updated_by") : null)
                            .reviewUpdatedAt(rs.getTimestamp("review_updated_at") != null ? rs.getTimestamp("review_updated_at").toLocalDateTime() : null)
                            .images(new ArrayList<>())
                            .reactions(new ArrayList<>())
                            .comments(new ArrayList<>())
                            .build();
                    reviewMap.put(reviewId, review);
                }

                // Process Review Image
                Long imageId = rs.getObject("image_id", Long.class);
                if (imageId != null && review.getImages().stream().noneMatch(i -> i.getImageId().equals(imageId))) {
                    review.getImages().add(ReviewDetailsResponse.ReviewImage.builder()
                            .imageId(imageId)
                            .imageUrl(rs.getString("image_url"))
                            .imageCreatedBy(rs.getLong("image_created_by"))
                            .imageCreatedAt(rs.getTimestamp("image_created_at").toLocalDateTime())
                            .build());
                }

                // Process Review Reaction
                Long reviewReactionId = rs.getObject("review_reaction_id", Long.class);
                if (reviewReactionId != null && review.getReactions().stream().noneMatch(r -> r.getReviewReactionId().equals(reviewReactionId))) {
                    review.getReactions().add(ReviewDetailsResponse.ReviewReaction.builder()
                            .reviewReactionId(reviewReactionId)
                            .reactionReviewId(rs.getLong("reaction_review_id"))
                            .userId(rs.getLong("reaction_user_id"))
                            .userName(rs.getString("reaction_user_name"))
                            .reactionType(rs.getString("reaction_type"))
                            .reviewReactionStatus(rs.getString("review_reaction_status"))
                            .reactionCreatedAt(rs.getTimestamp("reaction_created_at").toLocalDateTime())
                            .build());
                }

                // Process Comment
                Long commentId = rs.getObject("comment_id", Long.class);
                ReviewDetailsResponse.Comment comment = null;
                if (commentId != null) {
                    comment = review.getComments().stream()
                            .filter(c -> c.getCommentId().equals(commentId))
                            .findFirst()
                            .orElse(null);
                    if (comment == null) {
                        comment = ReviewDetailsResponse.Comment.builder()
                                .commentId(commentId)
                                .commentReviewId(rs.getLong("comment_review_id"))
                                .userId(rs.getLong("comment_user_id"))
                                .userName(rs.getString("comment_user_name"))
                                .parentCommentId(rs.getObject("parent_comment_id") != null ? rs.getLong("parent_comment_id") : null)
                                .comment(rs.getString("comment"))
                                .commentStatus(rs.getString("comment_status"))
                                .commentCreatedAt(rs.getTimestamp("comment_created_at").toLocalDateTime())
                                .commentCreatedBy(rs.getLong("comment_created_by"))
                                .commentReactions(new ArrayList<>())
                                .build();
                        review.getComments().add(comment);
                    }
                }

                // Process Comment Reaction
                if (comment != null) {
                    Long commentReactionId = rs.getObject("comment_reaction_id", Long.class);
                    if (commentReactionId != null && comment.getCommentReactions().stream().noneMatch(cr -> cr.getCommentReactionId().equals(commentReactionId))) {
                        comment.getCommentReactions().add(ReviewDetailsResponse.Comment.CommentReaction.builder()
                                .commentReactionId(commentReactionId)
                                .commentReactionCommentId(rs.getLong("comment_reaction_comment_id"))
                                .userId(rs.getLong("comment_reaction_user_id"))
                                .userName(rs.getString("comment_reaction_user_name"))
                                .commentReactionType(rs.getString("comment_reaction_type"))
                                .commentReactionStatus(rs.getString("comment_reaction_status"))
                                .commentReactionCreatedBy(rs.getLong("comment_reaction_created_by"))
                                .commentReactionCreatedAt(rs.getTimestamp("comment_reaction_created_at").toLocalDateTime())
                                .build());
                    }
                }
            });

            return new ArrayList<>(reviewMap.values());

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching reviews: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch reviews from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching reviews: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching reviews");
        }
    }

    @Override
    public ReviewAlreadyReactResponse isReviewAlreadyReacted(Long reviewId, Long userId) {
        try {
            return jdbcTemplate.queryForObject(
                    ReviewQueries.GET_REVIEW_PREVIOUS_REACT,
                    new Object[]{reviewId, userId},
                    (rs, rowNum) -> ReviewAlreadyReactResponse.builder()
                            .reviewId(rs.getLong("product_review_id"))
                            .userId(rs.getLong("user_id"))
                            .reactType(rs.getString("name"))
                            .status(rs.getLong("status"))
                            .build()
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addReactToReview(ReviewReactRequest reviewReactRequest, Long userId) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    ReviewQueries.ADD_REACTION_TO_REVIEW,
                    reviewReactRequest.getReviewId(),
                    userId,
                    userId,
                    reviewReactRequest.getReactType()
            );

            if (rowsAffected == 0) {
                throw new IllegalArgumentException(
                        "Invalid or inactive reaction type: " + reviewReactRequest.getReactType()
                );
            }

            LOGGER.info(
                    "Reaction '{}' added to review {} by user {}",
                    reviewReactRequest.getReactType(),
                    reviewReactRequest.getReviewId(),
                    userId
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Error while adding reaction to review", ex);
            throw ex;
        }
    }

    @Override
    public void removeReactToReview(ReviewReactRequest reviewReactRequest, Long userId) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    ReviewQueries.REMOVE_REVIEW_REACTION,
                    userId,
                    reviewReactRequest.getReviewId(),
                    userId
            );

            if (rowsAffected == 0) {
                LOGGER.warn(
                        "No active reaction found to remove for review {} by user {}",
                        reviewReactRequest.getReviewId(),
                        userId
                );
            } else {
                LOGGER.info(
                        "Reaction removed for review {} by user {}",
                        reviewReactRequest.getReviewId(),
                        userId
                );
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Error while removing reaction from review", ex);
            throw ex;
        }
    }

    @Override
    public void changeReactToReview(ReviewReactRequest reviewReactRequest, Long userId) {
        removeReactToReview(reviewReactRequest, userId);
        addReactToReview(reviewReactRequest, userId);
    }

    @Override
    public void addCommentToReview(ReviewCommentRequest reviewCommentRequest, Long userId) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    ReviewQueries.INSERT_REVIEW_COMMENT,
                    reviewCommentRequest.getReviewId(),
                    userId,
                    reviewCommentRequest.getParentId(),
                    reviewCommentRequest.getComment(),
                    userId
            );

            if (rowsAffected == 0) {
                throw new IllegalStateException("Failed to add comment to review");
            }

            LOGGER.info(
                    "Comment added to review {} by user {}",
                    reviewCommentRequest.getReviewId(),
                    userId
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Error while adding comment to review", ex);
            throw ex;
        }
    }

    @Override
    public ReviewCommentAlreadyReactResponse isReviewCommentAlreadyReacted(Long commentId, Long userId) {
        String IS_REVIEW_COMMENT_ALREADY_REACTED = ReviewQueries.IS_REVIEW_COMMENT_ALREADY_REACTED;
        try {
            return jdbcTemplate.queryForObject(
                    IS_REVIEW_COMMENT_ALREADY_REACTED
                    ,
                    new Object[]{commentId, userId},
                    (rs, rowNum) -> ReviewCommentAlreadyReactResponse.builder()
                            .commentId(rs.getLong("comment_id"))
                            .userId(rs.getLong("user_id"))
                            .reactType(rs.getString("name"))
                            .build()
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }    }

    @Override
    public void addReactToReviewComment(ReviewCommentReactRequest reviewCommentReactRequest, Long userId) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    ReviewQueries.ADD_REACTION_TO_REVIEW_COMMENT,
                    reviewCommentReactRequest.getCommentId(),
                    userId,
                    userId,
                    reviewCommentReactRequest.getReactType()
            );

            if (rowsAffected == 0) {
                throw new IllegalArgumentException(
                        "Invalid or inactive reaction type: " +
                                reviewCommentReactRequest.getReactType()
                );
            }

            LOGGER.info(
                    "Reaction '{}' added to comment {} by user {}",
                    reviewCommentReactRequest.getReactType(),
                    reviewCommentReactRequest.getCommentId(),
                    userId
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Error while reacting to review comment", ex);
            throw ex;
        }
    }

    @Override
    public void removeReactToReviewComment(ReviewCommentReactRequest reviewCommentReactRequest, Long userId) {
        String REMOVE_REVIEW_COMMENT_REACTION = ReviewQueries.REMOVE_REVIEW_COMMENT_REACTION;
        try {
            int rowsAffected = jdbcTemplate.update(
                    REMOVE_REVIEW_COMMENT_REACTION,
                    userId,
                    reviewCommentReactRequest.getCommentId(),
                    userId
            );

            if (rowsAffected == 0) {
                LOGGER.warn(
                        "No active reaction found to remove for comment {} by user {}",
                        reviewCommentReactRequest.getCommentId(),
                        userId
                );
            } else {
                LOGGER.info(
                        "Reaction removed for comment {} by user {}",
                        reviewCommentReactRequest.getCommentId(),
                        userId
                );
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Error while removing reaction from comment", ex);
            throw ex;
        }
    }

    @Override
    public void changeReactToReviewComment(ReviewCommentReactRequest reviewCommentReactRequest, Long userId) {
        removeReactToReviewComment(reviewCommentReactRequest,userId);
        addReactToReviewComment(reviewCommentReactRequest, userId);
    }

    @Override
    public Long addReviewDetails(InsertReviewRequest request, Long userId) {
        try {
            String sql = """
            INSERT INTO product_reviews (
                product_id,
                user_id,
                rating,
                comment,
                status,
                created_by,
                order_type,
                order_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                );

                ps.setLong(1, request.getProductOrPrintingId()); // product_id
                ps.setLong(2, userId);                           // user_id
                ps.setInt(3, request.getRating());               // rating (1–5)
                ps.setString(4, request.getComment());           // comment
                ps.setInt(5, 1);                                 // status (default active)
                ps.setLong(6, userId);                           // created_by
                ps.setString(7, request.getOrderType());         // order_type
                ps.setObject(8, request.getOrderId());           // order_id (nullable)

                return ps;
            }, keyHolder);

            return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        } catch (Exception e) {
            LOGGER.error("Error while adding review details: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void addReviewImages(List<InsertReviewRequest.ReviewImage> images,
                                Long reviewId,
                                Long userId) {

        try {
            if (images == null || images.isEmpty()) {
                return;
            }

            String sql = """
            INSERT INTO review_images (
                review_id,
                image_url,
                created_by
            ) VALUES (?, ?, ?)
        """;

            jdbcTemplate.batchUpdate(sql, images, images.size(), (ps, image) -> {
                ps.setLong(1, reviewId);
                ps.setString(2, image.getImageUrl());
                ps.setLong(3, userId);
            });

        } catch (Exception e) {
            LOGGER.error("Error while adding review images: {}", e.getMessage(), e);
        }
    }


}
