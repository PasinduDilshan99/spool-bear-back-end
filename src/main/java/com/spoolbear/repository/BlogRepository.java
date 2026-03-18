package com.spoolbear.repository;

import com.spoolbear.model.request.*;
import com.spoolbear.model.response.BlogAlreadyReactRespose;
import com.spoolbear.model.response.BlogCommentAlreadyReactResponse;
import com.spoolbear.model.response.BlogResponse;
import com.spoolbear.model.response.BlogTagResponse;

import java.util.List;

public interface BlogRepository {

    List<BlogResponse> getAllBlogs();

    BlogResponse getBlogDetailsById(BlogDetailsRequest blogDetailsRequest);

    void createBlog(CreateBlogRequest createBlogRequest, Long userId);

    List<BlogResponse> getBlogsByWriter(String writerName);

    List<Long> getAllBlogIdsByTagName(String tagName);

    List<BlogResponse> getBlogsByBlogsIdList(List<Long> blogIds);

    List<BlogTagResponse> getAllBlogTags();

    void updateBlogViewCountByOne(BlogDetailsRequest blogDetailsRequest);

    Boolean isBlogExists(Long blogId);

    void addBookmarkToBlog(BlogBookmarkRequest blogBookmarkRequest, Long userId);

    Boolean isBlogAlreadyBookmarked(Long blogId, Long userId);

    void removeBookmarkToBlog(BlogBookmarkRequest blogBookmarkRequest, Long userId);

    BlogAlreadyReactRespose isBlogAlreadyReacted(Long blogId, Long userId);

    void addReactToBlog(BlogReactRequest blogReactRequest, Long userId);

    void removeReactToBlog(BlogReactRequest blogReactRequest, Long userId);

    void changeReactToBlog(BlogReactRequest blogReactRequest, Long userId);

    void addCommentToBlog(BlogCommentRequest blogCommentRequest, Long userId);

    Boolean isBlogCommentExists(Long commentId);

    BlogCommentAlreadyReactResponse isBlogCommentAlreadyReacted(Long commentId, Long userId);

    void addReactToBlogComment(BlogCommentReactRequest blogCommentReactRequest, Long userId);

    void removeReactToBlogComment(BlogCommentReactRequest blogCommentReactRequest, Long userId);

    void changeReactToBlogComment(BlogCommentReactRequest blogCommentReactRequest, Long userId);

    List<BlogTagResponse> getAllBlogTagsByBLogId(Long blogId);
}
