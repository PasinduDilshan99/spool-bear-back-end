package com.spoolbear.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignOrderInsertRequest {
    private String customText;
    private String description;
    private String size;
    private String color;
    private String materiel;
    private int quantity;
}
