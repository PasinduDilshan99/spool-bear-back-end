package com.spoolbear.validations.impl;

import com.spoolbear.exception.ValidationFailedErrorExceptionHandler;
import com.spoolbear.model.request.*;
import com.spoolbear.model.response.ValidationFailedResponse;
import com.spoolbear.model.response.ValidationResultResponse;
import com.spoolbear.repository.BlogRepository;
import com.spoolbear.validations.BlogValidationService;
import com.spoolbear.validations.CommonValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class BlogValidationServiceImpl implements BlogValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlogValidationServiceImpl.class);

    private final CommonValidationService commonValidationService;
    private final BlogRepository blogRepository;

    @Autowired
    public BlogValidationServiceImpl(CommonValidationService commonValidationService,
                                     BlogRepository blogRepository) {
        this.commonValidationService = commonValidationService;
        this.blogRepository =blogRepository;
    }

    @Override
    public void validateCreateBlogRequest(CreateBlogRequest createBlogRequest) {
        List<ValidationFailedResponse> validationFailedResponses = new ArrayList<>();
        ValidationResultResponse title = commonValidationService.validateNotNullOrEmpty("title", createBlogRequest.getTitle());
        if (!title.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(title.getField()).value(title.getMessage()).build());
        }
        ValidationResultResponse subTitle = commonValidationService.validateNotNullOrEmpty("subTitle", createBlogRequest.getSubTitle());
        if (!subTitle.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(subTitle.getField()).value(subTitle.getMessage()).build());
        }
        ValidationResultResponse description = commonValidationService.validateNotNullOrEmpty("description", createBlogRequest.getDescription());
        if (!description.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(description.getField()).value(description.getMessage()).build());
        }

        int index = 0;
        for (String imageUrl : createBlogRequest.getImageUrls()) {
            ValidationResultResponse url = commonValidationService.validateNotNullOrEmpty("image url", imageUrl);
            if (!url.isValid()) {
                validationFailedResponses.add(
                        ValidationFailedResponse.builder()
                                .field(url.getField() + " - index " + index)
                                .value(url.getMessage())
                                .build()
                );
            }
            index++;
        }


        LOGGER.info(validationFailedResponses.toString());
        if (!validationFailedResponses.isEmpty()) {
            throw new ValidationFailedErrorExceptionHandler("Validation failed : createBlogRequest", validationFailedResponses);
        }
    }

    @Override
    public void validateBlogBookmarkRequest(BlogBookmarkRequest blogBookmarkRequest, Long userId) {
        List<ValidationFailedResponse> validationFailedResponses = new ArrayList<>();
        ValidationResultResponse blogId = commonValidationService.validateOnlyNumbers("blogId", blogBookmarkRequest.getBlogId().toString());
        if (!blogId.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(blogId.getField()).value(blogId.getMessage()).build());
        }
        Boolean isBlogExists = blogRepository.isBlogExists(blogBookmarkRequest.getBlogId());

        if (!isBlogExists) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field("blogId").value("Blog not found").build());
        }

//        Boolean isBlogAlreadyBookmarked = blogRepository.isBlogAlreadyBookmarked(blogBookmarkRequest.getBlogId(), userId);
//        if (isBlogAlreadyBookmarked) {
//            validationFailedResponses.add(ValidationFailedResponse.builder().field("blogId").value("Blog already bookmarked").build());
//        }

        if (!validationFailedResponses.isEmpty()) {
            throw new ValidationFailedErrorExceptionHandler("Validation failed : blogBookmarkRequest", validationFailedResponses);
        }
    }

    @Override
    public void validateBlogReactRequest(BlogReactRequest blogReactRequest) {
        List<ValidationFailedResponse> validationFailedResponses = new ArrayList<>();
        ValidationResultResponse blogId = commonValidationService.validateOnlyNumbers("blogId", blogReactRequest.getBlogId().toString());
        if (!blogId.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(blogId.getField()).value(blogId.getMessage()).build());
        }

        ValidationResultResponse reactionType = commonValidationService.validateRectionType("reactionType", blogReactRequest.getReactType());
        if (!reactionType.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(reactionType.getField()).value(reactionType.getMessage()).build());
        }

        Boolean isBlogExists = blogRepository.isBlogExists(blogReactRequest.getBlogId());
        if (!isBlogExists) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field("blogId").value("Blog not found").build());
        }


        if (!validationFailedResponses.isEmpty()) {
            throw new ValidationFailedErrorExceptionHandler("Validation failed : blogReactRequest", validationFailedResponses);
        }
    }

    @Override
    public void validateBlogCommentRequest(BlogCommentRequest blogCommentRequest) {
        List<ValidationFailedResponse> validationFailedResponses = new ArrayList<>();
        ValidationResultResponse blogId = commonValidationService.validateOnlyNumbers("blogId", blogCommentRequest.getBlogId().toString());
        if (!blogId.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(blogId.getField()).value(blogId.getMessage()).build());
        }

        ValidationResultResponse blogComment = commonValidationService.validateNotNull("blogComment", blogCommentRequest.getComment());
        if (!blogComment.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(blogComment.getField()).value(blogComment.getMessage()).build());
        }

        Boolean isBlogExists = blogRepository.isBlogExists(blogCommentRequest.getBlogId());
        if (!isBlogExists) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field("blogId").value("Blog not found").build());
        }


        if (!validationFailedResponses.isEmpty()) {
            throw new ValidationFailedErrorExceptionHandler("Validation failed : blogCommentRequest", validationFailedResponses);
        }
    }

    @Override
    public void validateBlogCommentReactRequest(BlogCommentReactRequest blogCommentReactRequest) {
        List<ValidationFailedResponse> validationFailedResponses = new ArrayList<>();
        ValidationResultResponse commentId = commonValidationService.validateOnlyNumbers("commentId", blogCommentReactRequest.getCommentId().toString());
        if (!commentId.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(commentId.getField()).value(commentId.getMessage()).build());
        }

        ValidationResultResponse reactionType = commonValidationService.validateRectionType("reactionType", blogCommentReactRequest.getReactType());
        if (!reactionType.isValid()) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field(reactionType.getField()).value(reactionType.getMessage()).build());
        }

        Boolean isBlogCommentExists = blogRepository.isBlogCommentExists(blogCommentReactRequest.getCommentId());
        if (!isBlogCommentExists) {
            validationFailedResponses.add(ValidationFailedResponse.builder().field("commentId").value("Blog comment not found").build());
        }


        if (!validationFailedResponses.isEmpty()) {
            throw new ValidationFailedErrorExceptionHandler("Validation failed : blogReactRequest", validationFailedResponses);
        }
    }
}
