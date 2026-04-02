package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private Double totalAmount;
    private String orderStatus;
    private String paymentStatus;
    private String status;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String orderType;
    private OrderItems orderItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItems{
        @Builder.Default
        private List<products> productsList = new ArrayList<>();
        @Builder.Default
        private List<printings> printingsList = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class products{
        private Long orderId;
        private Long productId;
        private String productName;
        private String productDescription;
        private List<Images> imagesList;
        private String productType;
        private String material;
        private int quantity;
        private String color;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class printings{
        private Long orderId;
        private Long printingOrderId;
        private Long productId;
        private String customText;
        private String description;
        private String size;
        private String color;
        private String material;
        private int quantity;
        private Double unitPrice;
        private String status;
        private Printer printer;
        private List<Images> imagesList;
        private List<OrderFiles> orderFilesList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderFiles{
        private Long fileId;
        private String fileName;
        private String fileUrl;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Printer{
        private Long printerId;
        private String printerName;
        private String printerModel;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Images{
        private String imageId;
        private String imageUrl;
        private boolean isPrimary;
        private String imageStatus;
        private Timestamp createdAt;
    }

}
