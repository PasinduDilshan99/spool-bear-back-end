package com.spoolbear.service;

import com.spoolbear.model.request.UserUpdateRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.UpdateResponse;
import com.spoolbear.model.response.UserProfileDetailsResponse;
import com.spoolbear.model.response.UserProfileSidebarResponse;

import java.util.List;

public interface UserProfileService {
    CommonResponse<UpdateResponse> updateUserProfileDetails(UserUpdateRequest userUpdateRequest);

    CommonResponse<List<UserProfileSidebarResponse>> getUserProfileSideBar();

    CommonResponse<UserProfileDetailsResponse> getUserProfileDetails();
}
