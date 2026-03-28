package com.spoolbear.service;

import com.spoolbear.model.request.UpdateUserNotificationPermissionRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.UpdateResponse;
import com.spoolbear.model.response.UserNotificationPermissionResponse;

public interface UserNotificationPermissionService {
    CommonResponse<UserNotificationPermissionResponse> getUserNotificationPermissionById();

    CommonResponse<UpdateResponse> updateUserNotificationPermissionDetails(UpdateUserNotificationPermissionRequest updateUserNotificationPermissionRequest);
}
