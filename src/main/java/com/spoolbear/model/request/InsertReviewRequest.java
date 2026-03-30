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
public class InsertReviewRequest {
    private Long productOrPrintingId;
    private int rating;
    private String comment;
    private Long orderId;
    private String orderType;
    private List<ReviewImage> images;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReviewImage{
        private String imageUrl;
    }
}
