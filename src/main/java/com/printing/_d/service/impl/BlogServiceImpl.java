package com.printing._d.service.impl;

import com.printing._d.exception.*;
import com.printing._d.model.enums.CommonStatus;
import com.printing._d.model.response.*;
import com.printing._d.repository.BlogRepository;
import com.printing._d.service.BlogService;
import com.printing._d.service.CommonService;
import com.printing._d.util.CommonResponseMessages;
import com.printing._d.validations.BlogValidationService;
import com.printing._d.model.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogServiceImpl.class);

    private final BlogRepository blogRepository;
    private final CommonService commonService;
    private final BlogValidationService blogValidationService;

    @Autowired
    public BlogServiceImpl(BlogRepository blogRepository,
                           CommonService commonService,
                           BlogValidationService blogValidationService) {
        this.blogRepository = blogRepository;
        this.commonService = commonService;
        this.blogValidationService = blogValidationService;
    }

    @Override
    public CommonResponse<List<BlogResponse>> getAllBlogs() {
        LOGGER.info("Start fetching all blogs from repository");
        try {
            List<BlogResponse> blogResponses = blogRepository.getAllBlogs();

            if (blogResponses.isEmpty()) {
                LOGGER.warn("No blogs found in database");
                throw new DataNotFoundErrorExceptionHandler("No blogs found");
            }

            LOGGER.info("Fetched {} blogs successfully", blogResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    blogResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching blogs: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blogs from database");
        } finally {
            LOGGER.info("End fetching all blogs from repository");
        }
    }

    @Override
    public CommonResponse<List<BlogResponse>> getAllActiveBlogs() {
        LOGGER.info("Start fetching active blogs from repository");

        try {
            List<BlogResponse> blogResponses = getAllBlogs().getData();

            List<BlogResponse> blogResponseList = blogResponses.stream()
                    .filter(item -> CommonStatus.ACTIVE.toString().equalsIgnoreCase(item.getBlogStatus()))
//                    .limit(6)   // take only the first 6
                    .toList();

            if (blogResponseList.isEmpty()) {
                LOGGER.warn("No active blogs found in database");
                throw new DataNotFoundErrorExceptionHandler("No active blogs found");
            }

            LOGGER.info("Fetched {} active blogs successfully", blogResponseList.size());

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    blogResponseList,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching active blogs: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blogs from database");
        } finally {
            LOGGER.info("End fetching active blogs from repository");
        }
    }

    @Override
    public CommonResponse<BlogResponse> getBlogDetailsById(BlogDetailsRequest blogDetailsRequest) {
        LOGGER.info("Start fetching blog details by id : {} from repository", blogDetailsRequest.getId());

        try {
            BlogResponse blogResponse = blogRepository.getBlogDetailsById(blogDetailsRequest);
            blogRepository.updateBlogViewCountByOne(blogDetailsRequest);
            Long userId = commonService.getUserIdBySecurityContextWithOutException();
            if (userId != null) {
                Boolean isBookmarked = blogRepository.isBlogAlreadyBookmarked(blogDetailsRequest.getId(), userId);
                blogResponse.setIsBookmark(isBookmarked);
            }

            LOGGER.info("Fetched blog details by id : {} successfully", blogDetailsRequest.getId());

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    blogResponse,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching blog details by id: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blog details by id : " + blogDetailsRequest.getId());
        } finally {
            LOGGER.info("End fetching blog details by id : {} from repository", blogDetailsRequest.getId());
        }
    }

    @Override
    public CommonResponse<InsertResponse> createBlog(CreateBlogRequest createBlogRequest) {
        try {
            blogValidationService.validateCreateBlogRequest(createBlogRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            blogRepository.createBlog(createBlogRequest, userId);

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    new InsertResponse("Successfully insert blog request"),
                    Instant.now());

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert blog request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<List<BlogResponse>> getBlogsByWriter(String writerName) {
        LOGGER.info("Start fetching blogs by writer from repository");
        try {
            List<BlogResponse> blogResponses = blogRepository.getBlogsByWriter(writerName);

            if (blogResponses.isEmpty()) {
                LOGGER.warn("No blogs from writer found in database");
                throw new DataNotFoundErrorExceptionHandler("No blogs from writer found");
            }

            LOGGER.info("Fetched {} blogs from writer successfully", blogResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    blogResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching blogs from writer: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blogs from writer from database");
        } finally {
            LOGGER.info("End fetching all blogs from writer from repository");
        }
    }

    @Override
    public CommonResponse<List<BlogResponse>> getBlogsByTagName(String tagName) {
        LOGGER.info("Start fetching all blogs by tag name from repository");
        try {
            List<Long> blogIds = blogRepository.getAllBlogIdsByTagName(tagName);
            List<BlogResponse> blogResponses = blogRepository.getBlogsByBlogsIdList(blogIds);

            if (blogResponses.isEmpty()) {
                LOGGER.warn("No blogs from tag name found in database");
                throw new DataNotFoundErrorExceptionHandler("No blogs from tag name found");
            }

            LOGGER.info("Fetched {} blogs from tag name successfully", blogResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    blogResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching blogs from writer: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blogs from tag name from database");
        } finally {
            LOGGER.info("End fetching all blogs from tag name from repository");
        }
    }

    @Override
    public CommonResponse<List<BlogTagResponse>> getAllBlogTags() {
        LOGGER.info("Start fetching all blog tags from repository");

        try {
            List<BlogTagResponse> blogTagResponses = blogRepository.getAllBlogTags();

            if (blogTagResponses.isEmpty()) {
                LOGGER.warn("No blog tags found in database");
                throw new DataNotFoundErrorExceptionHandler("No blog tags found");
            }

            List<BlogTagResponse> blogResponseList = blogTagResponses.stream()
                    .filter(item -> CommonStatus.ACTIVE.toString().equalsIgnoreCase(item.getStatusName()))
                    .toList();

            if (blogResponseList.isEmpty()) {
                LOGGER.warn("No active blog tags found in database");
                throw new DataNotFoundErrorExceptionHandler("No active blog tags found");
            }

            LOGGER.info("Fetched {} active blog tags successfully", blogResponseList.size());

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    blogResponseList,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching active blog tags: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blog tags from database");
        } finally {
            LOGGER.info("End fetching all active blog tags from repository");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addBookmarkToBlog(BlogBookmarkRequest blogBookmarkRequest) {
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            blogValidationService.validateBlogBookmarkRequest(blogBookmarkRequest, userId);
            Boolean isBookmarked = blogRepository.isBlogAlreadyBookmarked(blogBookmarkRequest.getBlogId(), userId);
            if (isBookmarked) {
                blogRepository.removeBookmarkToBlog(blogBookmarkRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_MESSAGE,
                        new InsertResponse("Successfully remove blog bookmark request"),
                        Instant.now()
                );
            } else {
                blogRepository.addBookmarkToBlog(blogBookmarkRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                        new InsertResponse("Successfully insert blog bookmark request"),
                        Instant.now()
                );
            }

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert blog bookmark request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());

        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addReactToBlog(BlogReactRequest blogReactRequest) {
        try {
            blogValidationService.validateBlogReactRequest(blogReactRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            BlogAlreadyReactRespose blogAlreadyReactRespose = blogRepository.isBlogAlreadyReacted(blogReactRequest.getBlogId(), userId);
            if (blogAlreadyReactRespose == null) {
                blogRepository.addReactToBlog(blogReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                        new InsertResponse("Successfully add blog react request"),
                        Instant.now()
                );
            } else if (blogAlreadyReactRespose.getReactType().equalsIgnoreCase(blogReactRequest.getReactType())) {
                blogRepository.removeReactToBlog(blogReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_MESSAGE,
                        new InsertResponse("Successfully remove blog react request"),
                        Instant.now()
                );
            } else {
                blogRepository.changeReactToBlog(blogReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                        new InsertResponse("Successfully change blog react request"),
                        Instant.now()
                );
            }

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert blog reaction request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addCommentToBlog(BlogCommentRequest blogCommentRequest) {
        try {
            blogValidationService.validateBlogCommentRequest(blogCommentRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            blogRepository.addCommentToBlog(blogCommentRequest, userId);
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    new InsertResponse("Successfully add comment to blog"),
                    Instant.now());

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert blog comment request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addReactToBlogComment(BlogCommentReactRequest blogCommentReactRequest) {
        try {
            blogValidationService.validateBlogCommentReactRequest(blogCommentReactRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            BlogCommentAlreadyReactResponse blogCommentAlreadyReactResponse = blogRepository.isBlogCommentAlreadyReacted(blogCommentReactRequest.getCommentId(), userId);
            if (blogCommentAlreadyReactResponse == null) {
                blogRepository.addReactToBlogComment(blogCommentReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                        new InsertResponse("Successfully add blog comment react request"),
                        Instant.now()
                );
            } else if (blogCommentAlreadyReactResponse.getReactType().equalsIgnoreCase(blogCommentReactRequest.getReactType())) {
                blogRepository.removeReactToBlogComment(blogCommentReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_TERMINATE_MESSAGE,
                        new InsertResponse("Successfully remove blog comment react request"),
                        Instant.now()
                );
            } else {
                blogRepository.changeReactToBlogComment(blogCommentReactRequest, userId);
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                        new InsertResponse("Successfully change blog comment react request"),
                        Instant.now()
                );
            }

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert blog comment reaction request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<List<BlogTagResponse>> getAllBlogTagsByBLogId(Long blogId) {
        LOGGER.info("Start fetching all blog tags by blog id : {} from repository",blogId);

        try {
            List<BlogTagResponse> blogTagResponses = blogRepository.getAllBlogTagsByBLogId(blogId);

            if (blogTagResponses.isEmpty()) {
                LOGGER.warn("No blog tags by blog id : {} found in database",blogId);
                throw new DataNotFoundErrorExceptionHandler("No blog tags by blog id : "+ blogId);
            }

            List<BlogTagResponse> blogResponseList = blogTagResponses.stream()
                    .filter(item -> CommonStatus.ACTIVE.toString().equalsIgnoreCase(item.getStatusName()))
                    .toList();

            if (blogResponseList.isEmpty()) {
                LOGGER.warn("No active blog tags by blog id : {} found in database",blogId);
                throw new DataNotFoundErrorExceptionHandler("No active blog tags by blog id : "+ blogId);
            }

            LOGGER.info("Fetched {} active blog tags by blog id : {} successfully", blogId,blogResponseList.size());

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    blogResponseList,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching active blog tags by blog id: {} , {}",blogId, e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blog tags by blog id : "+ blogId);
        } finally {
            LOGGER.info("End fetching all active blog tags by blog id : {} from repository" , blogId);
        }
    }

}
