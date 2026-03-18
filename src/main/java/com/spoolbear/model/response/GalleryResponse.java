package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GalleryResponse {
    private Integer imageId;
    private String imageName;
    private String imageDescription;
    private String location;
    private String imageLink;
    private String imageOwner;
    private String imageSource;
    private String imageSourceLink;
    private String color;
    private String hoverColor;
    private String imageStatus;
    private String imageStatusStatus;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp terminatedAt;
}
