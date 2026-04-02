package com.spoolbear.repository;

import com.spoolbear.model.request.UserProfileAddressInsertRequest;
import com.spoolbear.model.request.UserProfileDetailsRequest;
import com.spoolbear.model.request.UserUpdateRequest;
import com.spoolbear.model.response.UserProfileDetailsResponse;
import com.spoolbear.model.response.UserProfileSidebarResponse;

import java.util.List;

public interface UserProfileRepository {
    Long getUserProfileAddressId(Long userId);

    void updateUserProfileAddress(UserUpdateRequest userUpdateRequest, Long addressId);

    Long insertUserProfileAddress(UserProfileAddressInsertRequest userProfileAddressInsertRequest);

    void updateUserProfileDetails(UserUpdateRequest userUpdateRequest, Long userId);

    List<UserProfileSidebarResponse> getUserProfileSideBar();

    UserProfileDetailsResponse getUserProfileDetails(UserProfileDetailsRequest userProfileDetailsRequest);
}
