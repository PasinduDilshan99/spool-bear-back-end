package com.spoolbear.model.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBlogRequest {
    private String title;
    private String subTitle;
    private String description;
    private List<String> imageUrls;
}
