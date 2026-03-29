package com.spoolbear.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.ProductDetailsRequest;
import com.spoolbear.model.request.ProductsFilterRequest;
import com.spoolbear.model.response.GalleryResponse;
import com.spoolbear.model.response.ProductResponse;
import com.spoolbear.queries.GalleryQueries;
import com.spoolbear.repository.ProductsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductsRepositoryImpl implements ProductsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductsRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductResponse> getActiveProducts(ProductsFilterRequest filter) {
        try {
            LOGGER.info("Fetching active products with filters: {}", filter);

            StringBuilder sql = new StringBuilder("""
                    SELECT
                        p.product_id,
                        p.name AS product_name,
                        p.description AS product_description,
                        p.price,
                        p.stock_quantity,
                        p.is_customizable,
                        p.type_id,
                        pt.name AS type_name,
                        p.material_id,
                        m.name AS material_name,
                        m.description AS material_description,
                        pc.category_id,
                        pc.name AS category_name,
                        pi.image_id,
                        pi.image_url,
                        pi.is_primary,
                        pi.status AS image_status,
                        c.color_id,
                        c.name AS color_name
                    FROM products p
                    JOIN product_type pt ON p.type_id = pt.type_id
                    JOIN product_category pc ON pt.category_id = pc.category_id
                    LEFT JOIN materials m ON p.material_id = m.material_id
                    LEFT JOIN product_images pi ON pi.product_id = p.product_id AND pi.status = 1
                    LEFT JOIN product_color_mapping pcm ON pcm.product_id = p.product_id
                    LEFT JOIN product_colors c ON c.color_id = pcm.color_id AND c.status = 1
                    WHERE p.status = 1
                    """);

            List<Object> params = new ArrayList<>();

            // Dynamic filters
            if (filter.getCategoryId() != null) {
                sql.append(" AND pc.category_id = ?");
                params.add(filter.getCategoryId());
            }

            if (filter.getTypeId() != null) {
                sql.append(" AND pt.type_id = ?");
                params.add(filter.getTypeId());
            }

            if (filter.getMaterialId() != null) {
                sql.append(" AND m.material_id = ?");
                params.add(filter.getMaterialId());
            }

            if (filter.getMinPrice() != null) {
                sql.append(" AND p.price >= ?");
                params.add(filter.getMinPrice());
            }

            if (filter.getMaxPrice() != null) {
                sql.append(" AND p.price <= ?");
                params.add(filter.getMaxPrice());
            }

            if (filter.getInStock() != null && filter.getInStock()) {
                sql.append(" AND p.stock_quantity > 0");
            }

            if (filter.getName() != null && !filter.getName().isBlank()) {
                sql.append(" AND p.name LIKE ?");
                params.add("%" + filter.getName() + "%");
            }

            sql.append(" ORDER BY p.product_id, pi.is_primary DESC, pi.image_id");

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());

            // Group products and images
            Map<Long, ProductResponse> productMap = new LinkedHashMap<>();

            for (Map<String, Object> row : rows) {
                Long productId = ((Number) row.get("product_id")).longValue();
                ProductResponse product = productMap.get(productId);

                if (product == null) {
                    product = ProductResponse.builder()
                            .productId(productId)
                            .productName((String) row.get("product_name"))
                            .productDescription((String) row.get("product_description"))
                            .price((row.get("price") != null) ? ((Number) row.get("price")).doubleValue() : null)
                            .stockQuantity((row.get("stock_quantity") != null) ? ((Number) row.get("stock_quantity")).intValue() : null)
                            .isCustomizable((row.get("is_customizable") != null) ? (Boolean) row.get("is_customizable") : false)
                            .typeId((row.get("type_id") != null) ? ((Number) row.get("type_id")).longValue() : null)
                            .typeName((String) row.get("type_name"))
                            .materialId((row.get("material_id") != null) ? ((Number) row.get("material_id")).longValue() : null)
                            .materialName((String) row.get("material_name"))
                            .materialDescription((String) row.get("material_description"))
                            .categoryId((row.get("category_id") != null) ? ((Number) row.get("category_id")).longValue() : null)
                            .categoryName((String) row.get("category_name"))
                            .colors(new ArrayList<>())
                            .images(new ArrayList<>())
                            .build();

                    productMap.put(productId, product);
                }

                // Add color if exists
                if (row.get("color_name") != null) {
                    String colorName = (String) row.get("color_name");

                    // Avoid duplicates (because of JOIN with images)
                    if (!product.getColors().contains(colorName)) {
                        product.getColors().add(colorName);
                    }
                }

                // Add image if exists
                if (row.get("image_id") != null) {
                    ProductResponse.ProductImage image = new ProductResponse.ProductImage(
                            ((Number) row.get("image_id")).longValue(),
                            (String) row.get("image_url"),
                            (row.get("is_primary") != null) ? (Boolean) row.get("is_primary") : false,
                            (row.get("image_status") != null) ? ((Number) row.get("image_status")).intValue() : null
                    );
                    product.getImages().add(image);
                }
            }

            LOGGER.info("Fetched {} active products", productMap.size());
            return new ArrayList<>(productMap.values());

        } catch (Exception ex) {
            LOGGER.error("Error fetching active products", ex);
            throw new RuntimeException("Failed to fetch active products", ex);
        }
    }

    @Override
    public List<Long> getWishListProductIdsByUserId(Long userId) {
        String GET_WISH_LIST_PRODUCTS_BY_USER_ID =
                "SELECT product_id FROM wishlist WHERE user_id = ? AND status_id = 1";
        try {
            LOGGER.info("Executing query to fetch wishlist product IDs.");

            List<Long> wishListIds = jdbcTemplate.query(
                    GET_WISH_LIST_PRODUCTS_BY_USER_ID,
                    new Object[]{userId},
                    (rs, rowNum) -> rs.getLong("product_id")
            );

            LOGGER.info("Successfully fetched {} wishlist product IDs.", wishListIds.size());
            return wishListIds;

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching wishlist product IDs: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch wishlist product IDs from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching wishlist product IDs: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching wishlist product IDs");
        }
    }

    @Override
    public ProductResponse getProductDetailsById(ProductDetailsRequest request) {
        try {
            LOGGER.info("Fetching product details for productId: {}", request.getProductId());

            String sql = """
                SELECT
                    p.product_id,
                    p.name AS product_name,
                    p.description AS product_description,
                    p.price,
                    p.stock_quantity,
                    p.is_customizable,
                    p.type_id,
                    pt.name AS type_name,
                    p.material_id,
                    m.name AS material_name,
                    m.description AS material_description,
                    pc.category_id,
                    pc.name AS category_name,
                    pi.image_id,
                    pi.image_url,
                    pi.is_primary,
                    pi.status AS image_status,
                    c.color_id,
                    c.name AS color_name
                FROM products p
                JOIN product_type pt ON p.type_id = pt.type_id
                JOIN product_category pc ON pt.category_id = pc.category_id
                LEFT JOIN materials m ON p.material_id = m.material_id
                LEFT JOIN product_images pi ON pi.product_id = p.product_id AND pi.status = 1
                LEFT JOIN product_color_mapping pcm ON pcm.product_id = p.product_id
                LEFT JOIN product_colors c ON c.color_id = pcm.color_id AND c.status = 1
                WHERE p.status = 1
                  AND p.product_id = ?
                ORDER BY pi.is_primary DESC, pi.image_id
                """;

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, request.getProductId());

            if (rows.isEmpty()) {
                LOGGER.warn("No product found for productId: {}", request.getProductId());
                return null; // or throw exception
            }

            ProductResponse product = null;

            for (Map<String, Object> row : rows) {

                if (product == null) {
                    product = ProductResponse.builder()
                            .productId(((Number) row.get("product_id")).longValue())
                            .productName((String) row.get("product_name"))
                            .productDescription((String) row.get("product_description"))
                            .price(row.get("price") != null ? ((Number) row.get("price")).doubleValue() : null)
                            .stockQuantity(row.get("stock_quantity") != null ? ((Number) row.get("stock_quantity")).intValue() : null)
                            .isCustomizable(row.get("is_customizable") != null ? (Boolean) row.get("is_customizable") : false)
                            .typeId(row.get("type_id") != null ? ((Number) row.get("type_id")).longValue() : null)
                            .typeName((String) row.get("type_name"))
                            .materialId(row.get("material_id") != null ? ((Number) row.get("material_id")).longValue() : null)
                            .materialName((String) row.get("material_name"))
                            .materialDescription((String) row.get("material_description"))
                            .categoryId(row.get("category_id") != null ? ((Number) row.get("category_id")).longValue() : null)
                            .categoryName((String) row.get("category_name"))
                            .colors(new ArrayList<>())
                            .images(new ArrayList<>())
                            .build();
                }

                // ✅ Handle colors (avoid duplicates)
                if (row.get("color_name") != null) {
                    String colorName = (String) row.get("color_name");
                    if (!product.getColors().contains(colorName)) {
                        product.getColors().add(colorName);
                    }
                }

                // ✅ Handle images (avoid duplicates)
                if (row.get("image_id") != null) {
                    Long imageId = ((Number) row.get("image_id")).longValue();

                    boolean exists = product.getImages().stream()
                            .anyMatch(img -> img.getImageId().equals(imageId));

                    if (!exists) {
                        ProductResponse.ProductImage image = new ProductResponse.ProductImage(
                                imageId,
                                (String) row.get("image_url"),
                                row.get("is_primary") != null ? (Boolean) row.get("is_primary") : false,
                                row.get("image_status") != null ? ((Number) row.get("image_status")).intValue() : null
                        );
                        product.getImages().add(image);
                    }
                }
            }

            LOGGER.info("Successfully fetched product details for productId: {}", request.getProductId());
            return product;

        } catch (Exception ex) {
            LOGGER.error("Error fetching product details for productId: {}", request.getProductId(), ex);
            throw new RuntimeException("Failed to fetch product details", ex);
        }
    }

}
