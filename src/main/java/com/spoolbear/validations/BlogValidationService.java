package com.spoolbear.validations;

import com.spoolbear.model.request.*;

public interface BlogValidationService {
    void validateCreateBlogRequest(CreateBlogRequest createBlogRequest);

    void validateBlogBookmarkRequest(BlogBookmarkRequest blogBookmarkRequest, Long userId);

    void validateBlogReactRequest(BlogReactRequest blogReactRequest);

    void validateBlogCommentRequest(BlogCommentRequest blogCommentRequest);

    void validateBlogCommentReactRequest(BlogCommentReactRequest blogCommentReactRequest);
}
