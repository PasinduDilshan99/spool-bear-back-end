package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileSidebarResponse {

    private Integer id;
    private Integer parentId;
    private String name;
    private String description;
    private String privilegeName;
    private String status;
    private String url;
    private List<UserProfileSidebarResponse> children;
}