package com.printing._d.model.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogResponse {

    @JsonProperty("blog_id")
    private Long blogId;

    private String title;
    private String subtitle;
    private String description;
    private String blogCategory;
    private Long views;
    private Boolean isBookmark;

    @JsonProperty("writer_id")
    private Long writerId;

    @JsonProperty("writer_name")
    private String writerName;

    @JsonProperty("writer_image_url")
    private String writerImageUrl;

    @JsonProperty("blog_status")
    private String blogStatus;

    @JsonProperty("blog_created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime blogCreatedAt;

    private List<BlogImage> images;
    private int likeCount;

    @JsonProperty("blog_reactions")
    private List<BlogReaction> blogReactions;

    private List<CommentResponse> comments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlogImage {
        private Long id;

        @JsonProperty("image_url")
        private String imageUrl;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlogReaction {
        @JsonProperty("reaction_type_id")
        private Long reactionTypeId;

        @JsonProperty("reaction_type_name")
        private String reactionTypeName;

        private int count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentResponse {
        @JsonProperty("comment_id")
        private Long commentId;

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("image_url")
        private String imageUrl;
        private String username;
        private String comment;

        @JsonProperty("comment_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        private LocalDateTime commentDate;

        private List<CommentReaction> reactions;

        private List<CommentResponse> replies; // recursive replies
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentReaction {
        @JsonProperty("user_id")
        private Long userId;

        private String username;

        @JsonProperty("image_url")
        private String imageUrl;

        @JsonProperty("reaction_type")
        private String reactionType;
    }
}
