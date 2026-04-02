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
public class ProductOrderInsertRequest {
    private Double totalAmount;
    private List<OrderProducts> products;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderProducts{
        private Long cartItemId;
        private Long cartId;
        private Long productId;
        private Double price;
        private Integer quantity;
        private String color;
    }
}
