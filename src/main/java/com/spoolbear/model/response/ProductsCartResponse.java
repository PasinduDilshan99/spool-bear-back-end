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
public class ProductsCartResponse {

    private Long cartId;
    private Long cartItemId;
    private Long productId;
    private String name;
    private Double price;
    private Integer quantity;
    private List<Image> images;
    private Long materialId;
    private String material;
    private Long typeId;
    private String type;
    private String color;
    private String colorCode;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Image{
        private Long id;
        private String name;
        private String url;
        private String description;
    }
}
