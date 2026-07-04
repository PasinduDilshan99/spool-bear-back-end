package com.spoolbear.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.MaterialDetailsRequest;
import com.spoolbear.model.response.BlogResponse;
import com.spoolbear.model.response.MaterialDetailsResponse;
import com.spoolbear.queries.BlogQueries;
import com.spoolbear.queries.MaterialQueries;
import com.spoolbear.repository.MaterialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.spoolbear.queries.MaterialQueries.GET_ALL_MATERIALS;
import static com.spoolbear.queries.MaterialQueries.GET_MATERIAL_BY_ID;

@Repository
public class MaterialRepositoryImpl implements MaterialRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MaterialRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MaterialDetailsResponse> getAllMaterials() {
        try {
            LOGGER.info("Executing query to fetch all materials");

            return jdbcTemplate.query(GET_ALL_MATERIALS, (rs, rowNum) -> {

                // Map material type
                MaterialDetailsResponse.MaterialType materialType = MaterialDetailsResponse.MaterialType.builder()
                        .materialTypeId(rs.getLong("material_type_id"))
                        .name(rs.getString("material_type_name"))
                        .description(rs.getString("material_type_description"))
                        .iconUrl(rs.getString("material_type_icon"))
                        .typicalUseCases(rs.getString("typical_use_cases"))
                        .displayOrder(rs.getInt("display_order"))
                        .build();

                // Map images
                List<String> images = rs.getString("images") != null ?
                        List.of(rs.getString("images").split(",")) : List.of();

                // Map pros
                List<String> pros = rs.getString("pros") != null ?
                        List.of(rs.getString("pros").split(",")) : List.of();

                // Map cons
                List<String> cons = rs.getString("cons") != null ?
                        List.of(rs.getString("cons").split(",")) : List.of();

                // Map colors
                List<MaterialDetailsResponse.Color> colors = rs.getString("colors") != null ?
                        List.of(rs.getString("colors").split(",")) // each: name|hex|image
                                .stream()
                                .map(c -> {
                                    String[] parts = c.split("\\|");
                                    return MaterialDetailsResponse.Color.builder()
                                            .colorName(parts[0])
                                            .hexCode(parts.length > 1 ? parts[1] : null)
                                            .previewImage(parts.length > 2 ? parts[2] : null)
                                            .build();
                                }).toList() : List.of();

                // Map properties
                List<MaterialDetailsResponse.Property> properties = rs.getString("properties") != null ?
                        List.of(rs.getString("properties").split(",")) // each: name|value
                                .stream()
                                .map(p -> {
                                    String[] parts = p.split("\\|");
                                    return MaterialDetailsResponse.Property.builder()
                                            .propertyName(parts[0])
                                            .propertyValue(parts.length > 1 ? parts[1] : null)
                                            .build();
                                }).toList() : List.of();

                return MaterialDetailsResponse.builder()
                        .materialId(rs.getLong("material_id"))
                        .materialName(rs.getString("material_name"))
                        .materialDescription(rs.getString("material_description"))
                        .pricePerGram(rs.getBigDecimal("price_per_gram"))
                        .density(rs.getBigDecimal("density"))
                        .strength(rs.getString("strength"))
                        .flexibility(rs.getString("flexibility"))
                        .temperatureResistance(rs.getString("temperature_resistance"))
                        .finish(rs.getString("finish"))
                        .isPopular(rs.getBoolean("is_popular"))
                        .isAvailable(rs.getBoolean("is_available"))
                        .minLayerHeight(rs.getBigDecimal("min_layer_height"))
                        .maxLayerHeight(rs.getBigDecimal("max_layer_height"))
                        .materialType(materialType)
                        .images(images)
                        .pros(pros)
                        .cons(cons)
                        .colors(colors)
                        .properties(properties)
                        .build();
            });

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching materials: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch materials from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching materials: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching materials");
        }
    }

    @Override
    public MaterialDetailsResponse getMaterialsDetailsById(MaterialDetailsRequest materialDetailsRequest) {
        try {
            LOGGER.info("Fetching material details for ID: {}", materialDetailsRequest.getMaterialId());

            return jdbcTemplate.queryForObject(GET_MATERIAL_BY_ID, new Object[]{materialDetailsRequest.getMaterialId()}, (rs, rowNum) -> {

                // Map material type
                MaterialDetailsResponse.MaterialType materialType = MaterialDetailsResponse.MaterialType.builder()
                        .materialTypeId(rs.getLong("material_type_id"))
                        .name(rs.getString("material_type_name"))
                        .description(rs.getString("material_type_description"))
                        .iconUrl(rs.getString("material_type_icon"))
                        .typicalUseCases(rs.getString("typical_use_cases"))
                        .displayOrder(rs.getInt("display_order"))
                        .build();

                // Map images
                List<String> images = rs.getString("images") != null ?
                        List.of(rs.getString("images").split(",")) : List.of();

                // Map pros
                List<String> pros = rs.getString("pros") != null ?
                        List.of(rs.getString("pros").split(",")) : List.of();

                // Map cons
                List<String> cons = rs.getString("cons") != null ?
                        List.of(rs.getString("cons").split(",")) : List.of();

                // Map colors
                List<MaterialDetailsResponse.Color> colors = rs.getString("colors") != null ?
                        List.of(rs.getString("colors").split(",")) // each: name|hex|image
                                .stream()
                                .map(c -> {
                                    String[] parts = c.split("\\|");
                                    return MaterialDetailsResponse.Color.builder()
                                            .colorName(parts[0])
                                            .hexCode(parts.length > 1 ? parts[1] : null)
                                            .previewImage(parts.length > 2 ? parts[2] : null)
                                            .build();
                                }).toList() : List.of();

                // Map properties
                List<MaterialDetailsResponse.Property> properties = rs.getString("properties") != null ?
                        List.of(rs.getString("properties").split(",")) // each: name|value
                                .stream()
                                .map(p -> {
                                    String[] parts = p.split("\\|");
                                    return MaterialDetailsResponse.Property.builder()
                                            .propertyName(parts[0])
                                            .propertyValue(parts.length > 1 ? parts[1] : null)
                                            .build();
                                }).toList() : List.of();

                return MaterialDetailsResponse.builder()
                        .materialId(rs.getLong("material_id"))
                        .materialName(rs.getString("material_name"))
                        .materialDescription(rs.getString("material_description"))
                        .pricePerGram(rs.getBigDecimal("price_per_gram"))
                        .density(rs.getBigDecimal("density"))
                        .strength(rs.getString("strength"))
                        .flexibility(rs.getString("flexibility"))
                        .temperatureResistance(rs.getString("temperature_resistance"))
                        .finish(rs.getString("finish"))
                        .isPopular(rs.getBoolean("is_popular"))
                        .isAvailable(rs.getBoolean("is_available"))
                        .minLayerHeight(rs.getBigDecimal("min_layer_height"))
                        .maxLayerHeight(rs.getBigDecimal("max_layer_height"))
                        .materialType(materialType)
                        .images(images)
                        .pros(pros)
                        .cons(cons)
                        .colors(colors)
                        .properties(properties)
                        .build();
            });

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching material: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch material from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching material: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching material");
        }
    }
}
