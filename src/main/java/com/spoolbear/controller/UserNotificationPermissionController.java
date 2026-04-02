package com.spoolbear.controller;

import com.spoolbear.model.request.UpdateUserNotificationPermissionRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.UpdateResponse;
import com.spoolbear.model.response.UserNotificationPermissionResponse;
import com.spoolbear.service.UserNotificationPermissionService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v0/user-notification-permissions")
public class UserNotificationPermissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationPermissionController.class);

    private final UserNotificationPermissionService userNotificationPermissionService;

    @Autowired
    public UserNotificationPermissionController(UserNotificationPermissionService userNotificationPermissionService) {
        this.userNotificationPermissionService = userNotificationPermissionService;
    }

    @GetMapping(path = "/details")
    public ResponseEntity<CommonResponse<UserNotificationPermissionResponse>> getUserNotificationPermissionById() {
        LOGGER.info("{} Start execute get user notification permission details by id {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<UserNotificationPermissionResponse> response = userNotificationPermissionService.getUserNotificationPermissionById();
        LOGGER.info("{} End execute get user notification permission details by id {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/update")
    public ResponseEntity<CommonResponse<UpdateResponse>> updateUserNotificationPermissionDetails(@RequestBody UpdateUserNotificationPermissionRequest updateUserNotificationPermissionRequest) {
        LOGGER.info("{} Start execute update user notification permission details by id {}", Constant.DOTS, Constant.DOTS);
        LOGGER.info(updateUserNotificationPermissionRequest.toString());
        CommonResponse<UpdateResponse> response = userNotificationPermissionService.updateUserNotificationPermissionDetails(updateUserNotificationPermissionRequest);
        LOGGER.info("{} End execute update user notification permission details by id {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
