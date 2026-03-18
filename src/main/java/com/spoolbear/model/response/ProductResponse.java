package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long productId;
    private String productName;
    private String productDescription;
    private Double price;
    private Integer stockQuantity;
    private Boolean isCustomizable;

    private Long typeId;
    private String typeName;

    private Long materialId;
    private String materialName;
    private String materialDescription;

    private Long categoryId;
    private String categoryName;
    private List<ProductImage> images;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImage {
        private Long imageId;
        private String imageUrl;
        private Boolean isPrimary;
        private Integer status;
    }
}