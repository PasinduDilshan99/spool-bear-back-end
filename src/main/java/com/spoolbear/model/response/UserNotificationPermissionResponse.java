package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationPermissionResponse {

    private Long id;
    private Long userId;

    private Boolean newProductsUpdate;
    private LocalDateTime newProductsUpdateAt;

    private Boolean trackingUpdate;
    private LocalDateTime trackingUpdateAt;

    private Boolean productStatusUpdate;
    private LocalDateTime productStatusUpdateAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
