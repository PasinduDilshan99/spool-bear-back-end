package com.spoolbear.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InsertItemToCartRequest {

    private Long cartId;
    private Long productId;
    private Integer quantity;
    private String material;
    private Long materialId;
    private String type;
    private Long typeId;
    private String color;

}
