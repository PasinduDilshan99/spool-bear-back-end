package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDetailsResponse {

    private Long materialId;
    private String materialName;
    private String materialDescription;
    private BigDecimal pricePerGram;
    private BigDecimal density;
    private String strength;
    private String flexibility;
    private String temperatureResistance;
    private String finish;
    private Boolean isPopular;
    private Boolean isAvailable;
    private BigDecimal minLayerHeight;
    private BigDecimal maxLayerHeight;

    private MaterialType materialType;

    private List<String> images;
    private List<String> pros;
    private List<String> cons;
    private List<Color> colors;
    private List<Property> properties;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialType {
        private Long materialTypeId;
        private String name;
        private String description;
        private String iconUrl;
        private String typicalUseCases;
        private Integer displayOrder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Color {
        private String colorName;
        private String hexCode;
        private String previewImage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Property {
        private String propertyName;
        private String propertyValue;
    }
}