package com.spoolbear.repository;

import com.spoolbear.model.request.UpdateUserNotificationPermissionRequest;
import com.spoolbear.model.response.UserNotificationPermissionResponse;

public interface UserNotificationPermissionRepository {
    UserNotificationPermissionResponse getUserNotificationPermissionById(Long userId);

    void updateUserNotificationPermissionDetails(UpdateUserNotificationPermissionRequest updateUserNotificationPermissionRequest, Long userId);
}
