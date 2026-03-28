package com.spoolbear.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDetailsResponse {
    private Long reviewId;
    private Long productId;
    private String productName;
    private String reviewComment;
    private BigDecimal rating;
    private String reviewStatus;
    private Long reviewCreatedBy;
    private LocalDateTime reviewCreatedAt;
    private Long reviewUpdatedBy;
    private LocalDateTime reviewUpdatedAt;

    private List<ReviewImage> images;
    private List<ReviewReaction> reactions;
    private List<Comment> comments;

    @Data
    @Builder
    public static class ReviewImage {
        private Long imageId;
        private String imageUrl;
        private Long imageCreatedBy;
        private LocalDateTime imageCreatedAt;
    }

    @Data
    @Builder
    public static class ReviewReaction {
        private Long reviewReactionId;
        private Long reactionReviewId;
        private Long userId;
        private String userName;
        private String reactionType;
        private String reviewReactionStatus;
        private LocalDateTime reactionCreatedAt;
    }

    @Data
    @Builder
    public static class Comment {
        private Long commentId;
        private Long commentReviewId;
        private Long userId;
        private String userName;
        private Long parentCommentId;
        private String comment;
        private String commentStatus;
        private LocalDateTime commentCreatedAt;
        private Long commentCreatedBy;
        private List<CommentReaction> commentReactions;

        @Data
        @Builder
        public static class CommentReaction {
            private Long commentReactionId;
            private Long commentReactionCommentId;
            private Long userId;
            private String userName;
            private String commentReactionType;
            private String commentReactionStatus;
            private Long commentReactionCreatedBy;
            private LocalDateTime commentReactionCreatedAt;
        }
    }
}
