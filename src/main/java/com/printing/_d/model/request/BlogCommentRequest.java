package com.printing._d.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogCommentRequest {
    private Long blogId;
    private Long parentId;
    private String comment;
}
