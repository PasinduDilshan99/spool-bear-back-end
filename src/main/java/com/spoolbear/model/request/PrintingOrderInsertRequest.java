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
public class PrintingOrderInsertRequest {
    private String customText;
    private String description;
    private String size;
    private String color;
    private int quantity;
    private String materiel;
    private List<OrderFile> orderFiles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderFile {
        private String fileName;
        private String fileUrl;
    }

}
