package com.spoolbear.controller;

import com.spoolbear.model.request.UserUpdateRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.UpdateResponse;
import com.spoolbear.model.response.UserProfileDetailsResponse;
import com.spoolbear.model.response.UserProfileSidebarResponse;
import com.spoolbear.service.UserProfileService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/user-profile")
public class UserProfileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping(path = "/update-account")
    public ResponseEntity<CommonResponse<UpdateResponse>> updateUserProfileDetails(@RequestBody UserUpdateRequest userUpdateRequest) {
        LOGGER.info("{} Start execute update user profile details {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<UpdateResponse> response = userProfileService.updateUserProfileDetails(userUpdateRequest);
        LOGGER.info("{} End execute update user profile details {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/side-bar")
    public ResponseEntity<CommonResponse<List<UserProfileSidebarResponse>>> getUserProfileSideBar() {
        LOGGER.info("{} Start execute get user profile side bar details {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<UserProfileSidebarResponse>> response = userProfileService.getUserProfileSideBar();
        LOGGER.info("{} End execute get user profile side bar details {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/user")
    public ResponseEntity<CommonResponse<UserProfileDetailsResponse>> getUserProfileDetails() {
        LOGGER.info("{} Start execute get user profile details {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<UserProfileDetailsResponse> response = userProfileService.getUserProfileDetails();
        LOGGER.info("{} End execute get user profile details {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
