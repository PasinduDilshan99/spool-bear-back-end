package com.spoolbear.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintingOrderInsertRequestDto {
    private Long orderId;
    private Long productId;
    private String customText;
    private String description;
    private String size;
    private String color;
    private int quantity;
    private String status;
    private String materiel;
    private Long userId;
}
