package com.spoolbear.service;

import java.util.List;

import com.spoolbear.model.request.*;
import com.spoolbear.model.response.BlogResponse;
import com.spoolbear.model.response.BlogTagResponse;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;


public interface BlogService {
    CommonResponse<List<BlogResponse>> getAllBlogs();

    CommonResponse<List<BlogResponse>> getAllActiveBlogs();

    CommonResponse<BlogResponse> getBlogDetailsById(BlogDetailsRequest blogDetailsRequest);

    CommonResponse<InsertResponse> createBlog(CreateBlogRequest createBlogRequest);

    CommonResponse<List<BlogResponse>> getBlogsByWriter(String writerName);

    CommonResponse<List<BlogResponse>> getBlogsByTagName(String tagName);

    CommonResponse<List<BlogTagResponse>> getAllBlogTags();

    CommonResponse<InsertResponse> addBookmarkToBlog(BlogBookmarkRequest blogBookmarkRequest);

    CommonResponse<InsertResponse> addReactToBlog(BlogReactRequest blogReactRequest);

    CommonResponse<InsertResponse> addCommentToBlog(BlogCommentRequest blogCommentRequest);

    CommonResponse<InsertResponse> addReactToBlogComment(BlogCommentReactRequest blogCommentReactRequest);

    CommonResponse<List<BlogTagResponse>> getAllBlogTagsByBLogId(Long blogId);
}
