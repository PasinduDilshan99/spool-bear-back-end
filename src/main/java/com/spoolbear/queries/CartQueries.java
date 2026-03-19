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
            WHERE c.cart_id = ?
            ORDER BY ci.cart_item_id
            """ ;
}
