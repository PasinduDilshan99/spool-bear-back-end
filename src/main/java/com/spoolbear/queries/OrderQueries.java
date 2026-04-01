package com.spoolbear.queries;

public class OrderQueries {

    // 1. Get main order details by userId
    public static final String GET_ORDER_MAIN_DETAILS_BY_USER_ID = """
        SELECT 
            o.order_id,
            o.user_id,
            o.total_amount,
            o.order_status,
            o.payment_status,
            o.status,
            o.created_at,
            o.updated_at,
            o.order_type
        FROM orders o
        WHERE o.user_id = ?
        ORDER BY o.created_at DESC
    """;

    // 2. Get product order details by list of order IDs
    public static final String GET_PRODUCTS_DETAILS_BY_ORDER_ID_LIST_PREFIX = """
        SELECT
            oi.order_id,
            p.product_id,
            p.name AS product_name,
            p.description AS product_description,
            pt.name AS product_type,
            m.name AS material,
            oi.quantity,
            pc.name AS color_name,
            pi.image_id,
            pi.image_url,
            pi.is_primary,
            s.name AS image_status,
            pi.created_at AS image_created_at
        FROM order_items oi
        JOIN products p ON oi.product_id = p.product_id
        LEFT JOIN product_type pt ON p.type_id = pt.type_id
        LEFT JOIN materials m ON p.material_id = m.material_id
        LEFT JOIN product_colors pc ON oi.color_id = pc.color_id
        LEFT JOIN product_images pi ON pi.product_id = p.product_id AND pi.status = 1
        LEFT JOIN common_status s ON pi.status = s.id
        WHERE oi.order_id IN (
    """;

    // 3. Get printing orders by list of order IDs
    public static final String GET_PRINTINGS_DETAILS_BY_ORDER_ID_LIST_PREFIX = """
        SELECT
            po.order_id,
            po.printing_order_id,
            po.product_id,
            po.custom_text,
            po.description,
            po.size,
            po.color,
            m.name AS material,
            po.quantity,
            po.price AS unit_price,
            cs.name AS status,
            pr.printer_id,
            pr.name AS printer_name,
            pr.model AS printer_model,
            pi.image_id,
            pi.image_url,
            pi.is_primary AS image_is_primary,
            pis.name AS image_status,
            pi.created_at AS image_created_at,
            ofile.file_id,
            ofile.file_name,
            ofile.file_url
        FROM printing_orders po
        LEFT JOIN materials m ON po.material_id = m.material_id
        LEFT JOIN printers pr ON po.printer_id = pr.printer_id
        LEFT JOIN printing_images pi ON pi.printing_order_id = po.printing_order_id AND pi.status = 1
        LEFT JOIN common_status pis ON pi.status = pis.id
        LEFT JOIN order_files ofile ON ofile.printing_order_id = po.printing_order_id
        LEFT JOIN common_status cs ON po.status = cs.id
        WHERE po.order_id IN (
    """;
    public static final String GET_ORDER_MAIN_DETAILS_FOR_REVIEW_BY_USER_ID = """
            SELECT
            	o.order_id,
            	o.user_id,
            	o.total_amount,
            	o.order_status,
            	o.payment_status,
            	o.status,
            	o.created_at,
            	o.updated_at,
            	o.order_type
            FROM orders o
            WHERE o.user_id = ?
            AND o.order_status IN ('CANCELLED','DELIVERED','CONFIRMED','RETURNED')
            ORDER BY o.created_at DESC
            """;
    public static final String CHANGE_ORDER_STATUS = """
            UPDATE orders
            SET order_status = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE order_id = ?
            """;
}