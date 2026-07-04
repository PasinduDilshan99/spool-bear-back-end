package com.spoolbear.repository.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.InsertFailedErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.UpdateUserNotificationPermissionRequest;
import com.spoolbear.model.response.UserNotificationPermissionResponse;
import com.spoolbear.queries.UserNotificationPermissionQueries;
import com.spoolbear.repository.UserNotificationPermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class UserNotificationPermissionRepositoryImpl implements UserNotificationPermissionRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationPermissionRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserNotificationPermissionRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserNotificationPermissionResponse getUserNotificationPermissionById(Long userId) {
        String GET_USER_NOTIFICATION_PERMISSION_BY_ID = UserNotificationPermissionQueries.GET_USER_NOTIFICATION_PERMISSION_BY_ID;

        try {
            LOGGER.info("Executing query to fetch user notification permission details.");

            return jdbcTemplate.queryForObject(
                    GET_USER_NOTIFICATION_PERMISSION_BY_ID,
                    new Object[]{userId},
                    (rs, rowNum) -> UserNotificationPermissionResponse.builder()
                            .id(rs.getLong("id"))
                            .userId(rs.getLong("user_id"))
                            .newProductsUpdate(rs.getBoolean("new_products_update"))
                            .newProductsUpdateAt(rs.getObject("new_products_update_at", LocalDateTime.class))
                            .trackingUpdate(rs.getBoolean("tracking_update"))
                            .trackingUpdateAt(rs.getObject("tracking_update_at", LocalDateTime.class))
                            .productStatusUpdate(rs.getBoolean("product_status_update"))
                            .productStatusUpdateAt(rs.getObject("product_status_update_at", LocalDateTime.class))
                            .createdAt(rs.getObject("created_at", LocalDateTime.class))
                            .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                            .build()
            );

        } catch (DataAccessException ex) {
            LOGGER.error("Database error while fetching user notification permission details: {}", ex.getMessage(), ex);
            throw new DataAccessErrorExceptionHandler("Failed to fetch user notification permission from database");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error while fetching user notification permission details: {}", ex.getMessage(), ex);
            throw new InternalServerErrorExceptionHandler("Unexpected error occurred while fetching user notification permission");
        }
    }

    @Override
    public void updateUserNotificationPermissionDetails(UpdateUserNotificationPermissionRequest request, Long userId) {
        Map<String, String> columnMapping = Map.of(
                "new_products_update", "new_products_update_at",
                "tracking_update", "tracking_update_at",
                "product_status_update", "product_status_update_at"
        );

        String columnName = request.getName();
        Object value = request.getValue();

        if (!columnMapping.containsKey(columnName)) {
            throw new IllegalArgumentException("Invalid column name: " + columnName);
        }

        String timestampColumn = columnMapping.get(columnName);

        String sql = "UPDATE user_notification_permission " +
                "SET " + columnName + " = ?, " +
                timestampColumn + " = NOW() " +
                "WHERE user_id = ?";

        try {
            int rowsAffected = jdbcTemplate.update(sql, Boolean.valueOf(String.valueOf(value)), userId);

            if (rowsAffected == 0) {
                throw new InsertFailedErrorExceptionHandler("No rows affected when updating user notification");
            }

            LOGGER.info("Updated user notification {} for userId {} successfully.", columnName, userId);

        } catch (InsertFailedErrorExceptionHandler e) {
            LOGGER.error("Failed to update user notification {} for userId {}: {}", columnName, userId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while updating user notification {} for userId {}: {}", columnName, userId, e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Something went wrong while updating user notification");
        }
    }

}
