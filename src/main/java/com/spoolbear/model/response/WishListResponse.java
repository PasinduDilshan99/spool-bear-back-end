package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishListResponse {
    private Long productId;
    private String productName;
    private String productDescription;
    private String productDate;
    private List<String> productImages;
    private Double productPrice;
    private String productColor;
    private String productUrl;
    private Double discount;
    private String status;
    private Timestamp createdAt;
}
