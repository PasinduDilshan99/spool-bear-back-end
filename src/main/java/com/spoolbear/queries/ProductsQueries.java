package com.spoolbear.queries;

public class ProductsQueries {

    public static final String GET_ACTIVE_PRODUCTS_BY_GIVEN_PARAMS = """
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
            pi.status AS image_status
        FROM products p
        JOIN product_type pt ON p.type_id = pt.type_id
        JOIN product_category pc ON pt.category_id = pc.category_id
        LEFT JOIN materials m ON p.material_id = m.material_id
        LEFT JOIN product_images pi ON pi.product_id = p.product_id AND pi.status = 1
        WHERE p.status = 1
        AND (:categoryId IS NULL OR pc.category_id = :categoryId)
        AND (:typeId IS NULL OR pt.type_id = :typeId)
        AND (:materialId IS NULL OR m.material_id = :materialId)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:inStock IS NULL OR p.stock_quantity > 0)
        AND (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
        ORDER BY p.product_id, pi.is_primary DESC, pi.image_id
    """;
}
