package com.spoolbear.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderMainDetailsDto {
    private Long orderId;
    private Long userId;
    private Double totalAmount;
    private String orderStatus;
    private String paymentStatus;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String orderType;
}
