package com.printing._d.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.printing._d.exception.DataAccessErrorExceptionHandler;
import com.printing._d.exception.DataNotFoundErrorExceptionHandler;
import com.printing._d.exception.InsertFailedErrorExceptionHandler;
import com.printing._d.exception.InternalServerErrorExceptionHandler;
import com.printing._d.queries.BlogQueries;
import com.printing._d.repository.BlogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.printing._d.model.response.*;
import com.printing._d.model.request.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BlogRepositoryImpl implements BlogRepository {


    private static final Logger LOGGER = LoggerFactory.getLogger(BlogRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BlogRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<BlogResponse> getAllBlogs() {
        String GET_ALL_BLOGS = BlogQueries.GET_ALL_BLOGS;

        try {
            LOGGER.info("Executing query to fetch all blogs");

            List<BlogResponse> results = jdbcTemplate.query(GET_ALL_BLOGS, (rs, rowNum) -> {
                BlogResponse blog = new BlogResponse();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();

                blog.setBlogId(rs.getLong("blog_id"));
                blog.setTitle(rs.getString("title"));
                blog.setSubtitle(rs.getString("subtitle"));
                blog.setDescription(rs.getString("description"));
                blog.setWriterId(rs.getLong("writer_id"));
                blog.setViews(rs.getLong("views"));
                blog.setBlogCategory(rs.getString("blog_category"));
                blog.setWriterName(rs.getString("writer_name"));
                blog.setBlogStatus(rs.getString("blog_status"));
                blog.setBlogCreatedAt(rs.getTimestamp("blog_created_at").toLocalDateTime());

                // Parse images JSON
                String imagesJson = rs.getString("images");
                if (imagesJson != null) {
                    List<BlogResponse.BlogImage> images = null;
                    try {
                        images = objectMapper.readValue(
                                imagesJson,
                                new TypeReference<List<BlogResponse.BlogImage>>() {}
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    blog.setImages(images);
                }

                // Like count
                blog.setLikeCount(rs.getInt("like_count"));

                // Parse blog reactions JSON
                String reactionsJson = rs.getString("blog_reactions");
                if (reactionsJson != null) {
                    List<BlogResponse.BlogReaction> reactions = null;
                    try {
                        reactions = objectMapper.readValue(
                                reactionsJson,
                                new TypeReference<List<BlogResponse.BlogReaction>>() {}
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    blog.setBlogReactions(reactions);
                }

                // Parse comments JSON (nested replies + reactions)
                String commentsJson = rs.getString("comments");
                if (commentsJson != null) {
                    List<BlogResponse.CommentResponse> comments = null;
                    try {
                        comments = objectMapper.readValue(
                                commentsJson,
                                new TypeReference<List<BlogResponse.CommentResponse>>() {}
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    blog.setComments(comments);
                }

                return blog;
            });

            LOGGER.info("Successfully fetched {} blogs.", results.size());
            return results;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching blogs: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch blogs from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching blogs: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching blogs");
        }
    }

    @Override
    public BlogResponse getBlogDetailsById(BlogDetailsRequest blogDetailsRequest) {

        String GET_BLOG_DETAILS_BY_ID = BlogQueries.GET_BLOG_DETAILS_BY_ID;

        try {
            LOGGER.info("Executing query to fetch blog details for ID: {}", blogDetailsRequest.getId());

            BlogResponse blog = jdbcTemplate.queryForObject(
                    GET_BLOG_DETAILS_BY_ID,
                    new Object[]{blogDetailsRequest.getId()},
                    (rs, rowNum) -> {

                        BlogResponse response = new BlogResponse();
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.findAndRegisterModules();

                        response.setBlogId(rs.getLong("blog_id"));
                        response.setTitle(rs.getString("title"));
                        response.setSubtitle(rs.getString("subtitle"));
                        response.setDescription(rs.getString("description"));
                        response.setWriterId(rs.getLong("writer_id"));
                        response.setViews(rs.getLong("views"));
                        response.setBlogCategory(rs.getString("blog_category"));
                        response.setWriterName(rs.getString("writer_name"));
                        response.setWriterImageUrl(rs.getString("writer_image_url"));
                        response.setBlogStatus(rs.getString("blog_status"));
                        response.setBlogCreatedAt(rs.getTimestamp("blog_created_at").toLocalDateTime());

                        // Parse images JSON
                        String imagesJson = rs.getString("images");
                        if (imagesJson != null) {
                            try {
                                List<BlogResponse.BlogImage> images = objectMapper.readValue(
                                        imagesJson,
                                        new TypeReference<List<BlogResponse.BlogImage>>() {}
                                );
                                response.setImages(images);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException("Error parsing blog images JSON", e);
                            }
                        }

                        // Like count
                        response.setLikeCount(rs.getInt("like_count"));

                        // Parse reactions JSON
                        String reactionsJson = rs.getString("blog_reactions");
                        if (reactionsJson != null) {
                            try {
                                List<BlogResponse.BlogReaction> reactions = objectMapper.readValue(
                                        reactionsJson,
                                        new TypeReference<List<BlogResponse.BlogReaction>>() {}
                                );
                                response.setBlogReactions(reactions);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException("Error parsing blog reactions JSON", e);
                            }
                        }

                        String commentsJson = rs.getString("comments");
                        if (commentsJson != null) {
                            try {
                                List<BlogResponse.CommentResponse> comments = objectMapper.readValue(
                                        commentsJson,
                                        new TypeReference<List<BlogResponse.CommentResponse>>() {}
                                );
                                response.setComments(comments);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException("Error parsing blog comments JSON", e);
                            }
                        }

                        return response;
                    }
            );

            LOGGER.info("Successfully fetched blog details for ID {}", blog.getBlogId());
            return blog;

        } catch (EmptyResultDataAccessException ex) {
            LOGGER.error("Blog not found with ID: {}", blogDetailsRequest.getId());
            throw new DataNotFoundErrorExceptionHandler("Blog not found");
        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching blog details: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch blog details from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching blog details");
        }
    }

    @Override
    public void createBlog(CreateBlogRequest createBlogRequest, Long userId) {
        String INSERT_BLOG = BlogQueries.INSERT_BLOG;

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_BLOG, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, createBlogRequest.getTitle());
                ps.setString(2, createBlogRequest.getSubTitle());
                ps.setString(3, createBlogRequest.getDescription());
                ps.setLong(4, userId);
                ps.setLong(5, userId);
                ps.setLong(6, userId);
                return ps;
            }, keyHolder);

            if (rowsAffected == 0) {
                throw new InsertFailedErrorExceptionHandler("No rows affected when inserting blog data");
            }

            Number generatedId = keyHolder.getKey();
            if (generatedId == null) {
                throw new InsertFailedErrorExceptionHandler("Failed to retrieve inserted blog ID");
            }

            Long blogId = generatedId.longValue();
            LOGGER.info("Inserted Blog ID: {}", blogId);
            if (blogId > 0){
                insertImages(createBlogRequest,userId,blogId);
            }

        } catch (InsertFailedErrorExceptionHandler e) {
            throw new InsertFailedErrorExceptionHandler("Failed to insert blog record");
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public List<BlogResponse> getBlogsByWriter(String writerName) {
        String GET_BLOGS_BY_WRITER = BlogQueries.GET_BLOGS_BY_WRITER;

        try {
            LOGGER.info("Executing query to fetch blogs by writer.");

            List<BlogResponse> results = jdbcTemplate.query(GET_BLOGS_BY_WRITER, new Object[]{writerName},(rs, rowNum) -> {
                BlogResponse blog = new BlogResponse();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.findAndRegisterModules();

                blog.setBlogId(rs.getLong("blog_id"));
                blog.setTitle(rs.getString("title"));
                blog.setSubtitle(rs.getString("subtitle"));
                blog.setDescription(rs.getString("description"));
                blog.setWriterId(rs.getLong("writer_id"));
                blog.setViews(rs.getLong("views"));
                blog.setBlogCategory(rs.getString("blog_category"));
                blog.setWriterName(rs.getString("writer_name"));
                blog.setBlogStatus(rs.getString("blog_status"));
                blog.setBlogCreatedAt(rs.getTimestamp("blog_created_at").toLocalDateTime());

                // Parse images JSON
                String imagesJson = rs.getString("images");
                if (imagesJson != null) {
                    List<BlogResponse.BlogImage> images = null;
                    try {
                        images = objectMapper.readValue(
                                imagesJson,
                                new TypeReference<List<BlogResponse.BlogImage>>() {}
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    blog.setImages(images);
                }

                // Like count
                blog.setLikeCount(rs.getInt("like_count"));

                // Parse blog reactions JSON
                String reactionsJson = rs.getString("blog_reactions");
                if (reactionsJson != null) {
                    List<BlogResponse.BlogReaction> reactions = null;
                    try {
                        reactions = objectMapper.readValue(
                                reactionsJson,
                                new TypeReference<List<BlogResponse.BlogReaction>>() {}
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    blog.setBlogReactions(reactions);
                }

                // Parse comments JSON (nested replies + reactions)
                String commentsJson = rs.getString("comments");
                if (commentsJson != null) {
                    List<BlogResponse.CommentResponse> comments = null;
                    try {
                        comments = objectMapper.readValue(
                                commentsJson,
                                new TypeReference<List<BlogResponse.CommentResponse>>() {}
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    blog.setComments(comments);
                }

                return blog;
            });

            LOGGER.info("Successfully fetched {} blogs by writer.", results.size());
            return results;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching blogs by writer: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch blogs by writer from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching blogs by writer: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching blogs by writer");
        }
    }

    @Override
    public List<Long> getAllBlogIdsByTagName(String tagName) {
        String GET_BLOG_IDS_BY_TAG_NAME = BlogQueries.GET_BLOG_IDS_BY_TAG_NAME;
        try {
            return jdbcTemplate.query(
                    GET_BLOG_IDS_BY_TAG_NAME,
                    new Object[]{tagName},
                    (rs, rowNum) -> rs.getLong("blog_id")
            );
        } catch (Exception e) {
            LOGGER.error("Error fetching blog IDs for tag: {}", tagName, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<BlogResponse> getBlogsByBlogsIdList(List<Long> blogIds) {
        if (blogIds == null || blogIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            LOGGER.info("Fetching blogs for IDs: {}", blogIds);

            String inSql = blogIds.stream()
                    .map(id -> "?")
                    .collect(Collectors.joining(", "));

            String sql = BlogQueries.GET_BLOGS_BY_BLOG_IDS.replace(":ids", inSql);

            Object[] params = blogIds.toArray();

            return jdbcTemplate.query(
                    sql,
                    params,
                    (rs, rowNum) -> {
                        BlogResponse blog = new BlogResponse();
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.findAndRegisterModules();

                        blog.setBlogId(rs.getLong("blog_id"));
                        blog.setTitle(rs.getString("title"));
                        blog.setSubtitle(rs.getString("subtitle"));
                        blog.setDescription(rs.getString("description"));
                        blog.setWriterId(rs.getLong("writer_id"));
                        blog.setViews(rs.getLong("views"));
                        blog.setBlogCategory(rs.getString("blog_category"));
                        blog.setWriterName(rs.getString("writer_name"));
                        blog.setBlogStatus(rs.getString("blog_status"));
                        blog.setBlogCreatedAt(rs.getTimestamp("blog_created_at").toLocalDateTime());

                        // Images
                        String imagesJson = rs.getString("images");
                        if (imagesJson != null) {
                            try {
                                blog.setImages(objectMapper.readValue(imagesJson,
                                        new TypeReference<List<BlogResponse.BlogImage>>() {}));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // Like count
                        blog.setLikeCount(rs.getInt("like_count"));

                        // Blog reactions
                        String reactionsJson = rs.getString("blog_reactions");
                        if (reactionsJson != null) {
                            try {
                                blog.setBlogReactions(objectMapper.readValue(reactionsJson,
                                        new TypeReference<List<BlogResponse.BlogReaction>>() {}));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // Comments
                        String commentsJson = rs.getString("comments");
                        if (commentsJson != null) {
                            try {
                                blog.setComments(objectMapper.readValue(commentsJson,
                                        new TypeReference<List<BlogResponse.CommentResponse>>() {}));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        return blog;
                    }
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching blogs for ids: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch blogs for ids from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching blogs for ids: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching blogs for ids");
        }
    }

    @Override
    public List<BlogTagResponse> getAllBlogTags() {
        String GET_ALL_BLOG_TAGS = BlogQueries.GET_ALL_BLOG_TAGS;
        try {
            return jdbcTemplate.query(GET_ALL_BLOG_TAGS, (rs, rowNum) -> {
                BlogTagResponse blogTag = BlogTagResponse.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .statusId(rs.getInt("status_id"))
                        .statusName(rs.getString("status_name"))
                        .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                        .createdBy(rs.getObject("created_by") != null ? rs.getLong("created_by") : null)
                        .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                        .updatedBy(rs.getObject("updated_by") != null ? rs.getLong("updated_by") : null)
                        .terminatedAt(rs.getTimestamp("terminated_at") != null ? rs.getTimestamp("terminated_at").toLocalDateTime() : null)
                        .terminatedBy(rs.getObject("terminated_by") != null ? rs.getLong("terminated_by") : null)
                        .build();

                return blogTag;
            });
        } catch (Exception e) {
            LOGGER.error("Error fetching blog tags: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blog tags from database");
        }
    }

    @Override
    public void updateBlogViewCountByOne(BlogDetailsRequest blogDetailsRequest) {
        String UPDATE_BLOG_VIEW_COUNT_BY_ONE = BlogQueries.UPDATE_BLOG_VIEW_COUNT_BY_ONE;

        try {
            int rowsAffected = jdbcTemplate.update(
                    UPDATE_BLOG_VIEW_COUNT_BY_ONE,
                    blogDetailsRequest.getId()
            );

            if (rowsAffected == 0) {
                LOGGER.warn("No blog found to update view count for id: {}", blogDetailsRequest.getId());
            }

        } catch (Exception e) {
            LOGGER.error("Error updating blog view count for id {}: {}", blogDetailsRequest.getId(), e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to update blog view count");
        }
    }

    @Override
    public Boolean isBlogExists(Long blogId) {
        String IS_BLOG_EXISTS = BlogQueries.IS_BLOG_EXISTS;

        try {
            Integer count = jdbcTemplate.queryForObject(
                    IS_BLOG_EXISTS,
                    new Object[]{blogId},
                    Integer.class
            );
            return count != null && count > 0;

        } catch (Exception e) {
            LOGGER.error("Failed to check blog exists: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Error checking blog existence");
        }
    }

    @Override
    public void addBookmarkToBlog(BlogBookmarkRequest blogBookmarkRequest, Long userId) {
        String INSERT_BLOG_BOOKMARK = BlogQueries.INSERT_BLOG_BOOKMARK;

        try {
            int rows = jdbcTemplate.update(
                    INSERT_BLOG_BOOKMARK,
                    userId,
                    blogBookmarkRequest.getBlogId(),
                    userId
            );

            if (rows <= 0) {
                throw new DataAccessException("Failed to insert bookmark") {};
            }

            LOGGER.info("Bookmark added: user {} -> blog {}", userId, blogBookmarkRequest.getBlogId());

        } catch (DuplicateKeyException e) {
            LOGGER.warn("Bookmark already exists: user {} -> blog {}", userId, blogBookmarkRequest.getBlogId());
            throw new InsertFailedErrorExceptionHandler("This blog is already bookmarked");

        } catch (Exception e) {
            LOGGER.error("Error adding bookmark: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to add bookmark");
        }
    }

    @Override
    public Boolean isBlogAlreadyBookmarked(Long blogId, Long userId) {
        String IS_BLOG_ALREADY_BOOKMARKED = BlogQueries.IS_BLOG_ALREADY_BOOKMARKED;

        try {
            Integer count = jdbcTemplate.queryForObject(
                    IS_BLOG_ALREADY_BOOKMARKED,
                    new Object[]{blogId, userId},
                    Integer.class
            );

            return count != null && count > 0;

        } catch (Exception e) {
            LOGGER.error("Error checking bookmark existence: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to check bookmark status");
        }
    }

    @Override
    public void removeBookmarkToBlog(BlogBookmarkRequest blogBookmarkRequest, Long userId) {
        String REMOVE_BLOG_BOOKMARK = BlogQueries.REMOVE_BLOG_BOOKMARK;

        try {
            int rowsAffected = jdbcTemplate.update(
                    REMOVE_BLOG_BOOKMARK,
                    userId,
                    blogBookmarkRequest.getBlogId(),
                    userId
            );

            if (rowsAffected == 0) {
                LOGGER.warn("No active bookmark found to remove for user {} and blog {}", userId, blogBookmarkRequest.getBlogId());
                throw new DataNotFoundErrorExceptionHandler("Bookmark not found or already removed");
            }

            LOGGER.info("Bookmark removed for user {} -> blog {}", userId, blogBookmarkRequest.getBlogId());

        } catch (Exception e) {
            LOGGER.error("Error removing bookmark: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to remove bookmark");
        }
    }

    @Override
    public BlogAlreadyReactRespose isBlogAlreadyReacted(Long blogId, Long userId) {
        try {
            return jdbcTemplate.queryForObject(
                    BlogQueries.GET_BLOG_PREVIOUS_REACT,
                    new Object[]{blogId, userId},
                    (rs, rowNum) -> BlogAlreadyReactRespose.builder()
                            .blogId(rs.getLong("blog_id"))
                            .userId(rs.getLong("user_id"))
                            .reactType(rs.getString("name"))
                            .build()
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addReactToBlog(BlogReactRequest blogReactRequest, Long userId) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    BlogQueries.ADD_REACTION_TO_BLOG,
                    blogReactRequest.getBlogId(),
                    userId,
                    userId,
                    blogReactRequest.getReactType()
            );

            if (rowsAffected == 0) {
                throw new IllegalArgumentException(
                        "Invalid or inactive reaction type: " + blogReactRequest.getReactType()
                );
            }

            LOGGER.info(
                    "Reaction '{}' added to blog {} by user {}",
                    blogReactRequest.getReactType(),
                    blogReactRequest.getBlogId(),
                    userId
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Error while adding reaction to blog", ex);
            throw ex;
        }
    }


    @Override
    public void removeReactToBlog(BlogReactRequest blogReactRequest, Long userId) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    BlogQueries.REMOVE_BLOG_REACTION,
                    userId,
                    blogReactRequest.getBlogId(),
                    userId
            );

            if (rowsAffected == 0) {
                LOGGER.warn(
                        "No active reaction found to remove for blog {} by user {}",
                        blogReactRequest.getBlogId(),
                        userId
                );
            } else {
                LOGGER.info(
                        "Reaction removed for blog {} by user {}",
                        blogReactRequest.getBlogId(),
                        userId
                );
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Error while removing reaction from blog", ex);
            throw ex;
        }
    }

    @Override
    public void changeReactToBlog(BlogReactRequest blogReactRequest, Long userId) {
        removeReactToBlog(blogReactRequest, userId);
        addReactToBlog(blogReactRequest, userId);
    }

    @Override
    public void addCommentToBlog(BlogCommentRequest blogCommentRequest, Long userId) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    BlogQueries.INSERT_BLOG_COMMENT,
                    blogCommentRequest.getBlogId(),
                    userId,
                    blogCommentRequest.getParentId(),
                    blogCommentRequest.getComment(),
                    userId
            );

            if (rowsAffected == 0) {
                throw new IllegalStateException("Failed to add comment to blog");
            }

            LOGGER.info(
                    "Comment added to blog {} by user {}",
                    blogCommentRequest.getBlogId(),
                    userId
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Error while adding comment to blog", ex);
            throw ex;
        }
    }

    @Override
    public Boolean isBlogCommentExists(Long commentId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    BlogQueries.CHECK_BLOG_COMMENT_EXISTS,
                    Integer.class,
                    commentId
            );

            return count != null && count > 0;

        } catch (DataAccessException ex) {
            LOGGER.error("Error while checking blog comment existence for id {}", commentId, ex);
            return false;
        }
    }

    @Override
    public BlogCommentAlreadyReactResponse isBlogCommentAlreadyReacted(Long commentId, Long userId) {
        String IS_BLOG_COMMENT_ALREADY_REACTED = BlogQueries.IS_BLOG_COMMENT_ALREADY_REACTED;
        try {
            return jdbcTemplate.queryForObject(
                    IS_BLOG_COMMENT_ALREADY_REACTED
                    ,
                    new Object[]{commentId, userId},
                    (rs, rowNum) -> BlogCommentAlreadyReactResponse.builder()
                            .commentId(rs.getLong("comment_id"))
                            .userId(rs.getLong("user_id"))
                            .reactType(rs.getString("name"))
                            .build()
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addReactToBlogComment(BlogCommentReactRequest blogCommentReactRequest, Long userId) {
        try {
            int rowsAffected = jdbcTemplate.update(
                    BlogQueries.ADD_REACTION_TO_BLOG_COMMENT,
                    blogCommentReactRequest.getCommentId(),
                    userId,
                    userId,
                    blogCommentReactRequest.getReactType()
            );

            if (rowsAffected == 0) {
                throw new IllegalArgumentException(
                        "Invalid or inactive reaction type: " +
                                blogCommentReactRequest.getReactType()
                );
            }

            LOGGER.info(
                    "Reaction '{}' added to comment {} by user {}",
                    blogCommentReactRequest.getReactType(),
                    blogCommentReactRequest.getCommentId(),
                    userId
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Error while reacting to blog comment", ex);
            throw ex;
        }
    }

    @Override
    public void removeReactToBlogComment(BlogCommentReactRequest blogCommentReactRequest, Long userId) {
        String REMOVE_BLOG_COMMENT_REACTION = BlogQueries.REMOVE_BLOG_COMMENT_REACTION;
        try {
            int rowsAffected = jdbcTemplate.update(
                    REMOVE_BLOG_COMMENT_REACTION,
                    userId,
                    blogCommentReactRequest.getCommentId(),
                    userId
            );

            if (rowsAffected == 0) {
                LOGGER.warn(
                        "No active reaction found to remove for comment {} by user {}",
                        blogCommentReactRequest.getCommentId(),
                        userId
                );
            } else {
                LOGGER.info(
                        "Reaction removed for comment {} by user {}",
                        blogCommentReactRequest.getCommentId(),
                        userId
                );
            }

        } catch (DataAccessException ex) {
            LOGGER.error("Error while removing reaction from comment", ex);
            throw ex;
        }
    }

    @Override
    public void changeReactToBlogComment(BlogCommentReactRequest blogCommentReactRequest, Long userId) {
        removeReactToBlogComment(blogCommentReactRequest,userId);
        addReactToBlogComment(blogCommentReactRequest, userId);
    }

    @Override
    public List<BlogTagResponse> getAllBlogTagsByBLogId(Long blogId) {
        String GET_ALL_BLOG_TAGS_BY_BLOG_ID = BlogQueries.GET_ALL_BLOG_TAGS_BY_BLOG_ID;
        try {
            return jdbcTemplate.query(GET_ALL_BLOG_TAGS_BY_BLOG_ID, new Object[]{blogId},(rs, rowNum) -> {
                BlogTagResponse blogTag = BlogTagResponse.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .statusId(rs.getInt("status_id"))
                        .statusName(rs.getString("status_name"))
                        .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                        .createdBy(rs.getObject("created_by") != null ? rs.getLong("created_by") : null)
                        .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                        .updatedBy(rs.getObject("updated_by") != null ? rs.getLong("updated_by") : null)
                        .terminatedAt(rs.getTimestamp("terminated_at") != null ? rs.getTimestamp("terminated_at").toLocalDateTime() : null)
                        .terminatedBy(rs.getObject("terminated_by") != null ? rs.getLong("terminated_by") : null)
                        .build();

                return blogTag;
            });
        } catch (Exception e) {
            LOGGER.error("Error fetching blog tags: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch blog tags from database");
        }
    }

    public void insertImages(CreateBlogRequest createBlogRequest, Long userId, Long blogId) {
        String INSERT_BLOG_IMAGES = BlogQueries.INSERT_BLOG_IMAGES;
        try {
            for (String image: createBlogRequest.getImageUrls()){
                int rowsAffected = jdbcTemplate.update(INSERT_BLOG_IMAGES,
                        blogId,
                        image,
                        userId,
                        userId
                );
                if (rowsAffected == 0) {
                    throw new InsertFailedErrorExceptionHandler("No rows affected when inserting blog image");
                }
            }

        } catch (InsertFailedErrorExceptionHandler e) {
            throw new InsertFailedErrorExceptionHandler("Failed to insert blog image record");
        } catch (Exception e) {
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

}
