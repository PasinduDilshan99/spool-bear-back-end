package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogTagResponse {
    private Long id;

    private String name;

    private String description;

    private Integer statusId;

    private String statusName;

    private LocalDateTime createdAt;

    private Long createdBy;

    private LocalDateTime updatedAt;

    private Long updatedBy;

    private LocalDateTime terminatedAt;

    private Long terminatedBy;
}