package com.spoolbear.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInsertRequestDto {
    private Long userId;
    private Double totalAmount;
    private String orderStatus;
    private Integer status;
    private String orderType;
    private String paymentStatus;
}
