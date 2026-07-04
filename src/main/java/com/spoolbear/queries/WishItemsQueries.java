package com.spoolbear.queries;

public class WishItemsQueries {
    public static final String GET_ALL_WISHLIST_ITEMS = """
                SELECT
                    w.id AS wishlist_id,
                    p.product_id,
                    p.name AS product_name,
                    p.description,
                    p.price,
                    p.created_at,
                    cs.name AS status_name,
                    pi.image_url
                FROM wishlist w
                JOIN products p ON w.product_id = p.product_id
                LEFT JOIN product_images pi ON pi.product_id = p.product_id
                LEFT JOIN common_status cs ON w.status_id = cs.id
                WHERE w.user_id = ?
                  AND cs.name = 'ACTIVE'
                ORDER BY p.product_id
            """;

    public static final String INSERT_WISH_DATA = """
                INSERT INTO wishlist (user_id, product_id, status_id)
                VALUES (?, ?, (SELECT id FROM common_status WHERE name = 'ACTIVE' LIMIT 1))
                ON DUPLICATE KEY UPDATE
                    status_id = (SELECT id FROM common_status WHERE name = 'ACTIVE' LIMIT 1),
                    updated_at = CURRENT_TIMESTAMP
            """;

    public static final String INSERT_WISHLIST_HISTORY = """
                INSERT INTO wishlist_history (wishlist_id, status_id)
                VALUES (?, (SELECT id FROM common_status WHERE name = ? LIMIT 1))
            """;


    public static final String UPDATE_WISHLIST_STATUS = """
                UPDATE wishlist
                SET status_id = (SELECT id FROM common_status WHERE name = ? LIMIT 1),
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ? AND user_id = ?
            """;

    public static final String GET_EXISTING_WISHLIST_DATA = """
                SELECT
                    w.id AS wishListId,
                    w.product_id AS productId,
                    w.user_id AS userId,
                    cs.name AS status,
                    w.created_at AS createdAt,
                    w.updated_at AS updatedAt
                FROM wishlist w
                JOIN common_status cs ON w.status_id = cs.id
                WHERE w.user_id = ?
                  AND w.product_id = ?
                LIMIT 1
            """;
}
