package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewAlreadyReactResponse {
    private Long reviewId;
    private String reactType;
    private Long userId;
    private Long status;
}
