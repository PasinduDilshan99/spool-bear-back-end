package com.spoolbear.queries;

public class UserNotificationPermissionQueries {

    public static final String GET_USER_NOTIFICATION_PERMISSION_BY_ID = """
            SELECT
                id,
                user_id,
                new_products_update,
                new_products_update_at,
                tracking_update,
                tracking_update_at,
                product_status_update,
                product_status_update_at,
                created_at,
                updated_at
            FROM user_notification_permission
            WHERE user_id = ?
            """;
}
