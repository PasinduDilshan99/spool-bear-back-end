package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NavBarSubmenuResponse {
    private Integer id;
    private Integer navBarId;
    private String name;
    private String description;
    private String linkUrl;
    private String iconClass;
    private Integer sortOrder;
    private Boolean opensInNewTab;
    private Boolean isFeatured;
    private String status;
    private LocalDateTime createdAt;
    private Integer createdBy;
    private LocalDateTime updatedAt;
    private Integer updatedBy;
    private LocalDateTime terminatedAt;
    private Integer terminatedBy;
}