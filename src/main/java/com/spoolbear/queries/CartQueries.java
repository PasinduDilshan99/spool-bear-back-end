package com.spoolbear.queries;

public class CartQueries {
    public static final String GET_PRODUCTS_BY_CART_ID = """
            SELECT
                c.cart_id,
                ci.cart_item_id,
                p.product_id,
                p.name AS product_name,
                p.price,
                ci.quantity,
                m.material_id,
                m.name AS material_name,
                pt.type_id,
                pt.name AS type_name,
                pc.color_id,
                pc.name AS color_name,
                pc.hex_code AS color_code,
                pi.image_id,
                pi.image_url
            FROM cart_items ci
            JOIN cart c ON ci.cart_id = c.cart_id
            JOIN products p ON ci.product_id = p.product_id
            LEFT JOIN materials m ON p.material_id = m.material_id
            LEFT JOIN product_type pt ON p.type_id = pt.type_id
            LEFT JOIN product_colors pc ON ci.product_color_id = pc.color_id
            LEFT JOIN product_images pi ON pi.product_id = p.product_id
            LEFT JOIN common_status cs ON ci.status = cs.id
            WHERE c.cart_id = ?
            AND cs.name = 'ACTIVE'
            AND ci.quantity > 0
            ORDER BY ci.cart_item_id
            """ ;

    public static final String GET_COLOR_ID_BY_NAME = """
    SELECT color_id FROM product_colors WHERE name = ? LIMIT 1
""";

    public static final String INSERT_CART_ITEM = """
    INSERT INTO cart_items (cart_id, product_id, product_color_id, quantity, created_by)
    VALUES (?, ?, ?, ?, ?)
""";

    public static final String DECREASE_QUANTITY = """
    UPDATE cart_items
    SET quantity = quantity - 1
    WHERE cart_item_id = ? AND quantity > 0
""";

    public static final String GET_TERMINATED_STATUS_ID = """
    SELECT id FROM common_status WHERE name = 'TERMINATED' LIMIT 1
""";

    public static final String REMOVE_CART_ITEM = """
    UPDATE cart_items
    SET status = ?
    WHERE cart_item_id = ?
""";

    public static final String INCREASE_QUANTITY = """
    UPDATE cart_items
    SET quantity = quantity + 1
    WHERE cart_item_id = ? AND (status IS NULL OR status = 1)
""";

    public static final String INCREASE_PRODUCT_STOCK = """
    UPDATE products
    SET stock_quantity = stock_quantity + 1
    WHERE product_id = ?
""";

    public static final String DECREASE_PRODUCT_STOCK = """
    UPDATE products
    SET stock_quantity = stock_quantity - 1
    WHERE product_id = ? AND stock_quantity > 0
""";

    public static final String GET_CART_ITEM_QUANTITY = """
    SELECT quantity 
    FROM cart_items 
    WHERE cart_item_id = ? 
      AND (status IS NULL OR status = 1)
""";
}
