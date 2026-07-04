package com.spoolbear.service.impl;

import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.exception.UpdateFailedErrorExceptionHandler;
import com.spoolbear.model.enums.CommonStatus;
import com.spoolbear.model.request.UserProfileAddressInsertRequest;
import com.spoolbear.model.request.UserProfileDetailsRequest;
import com.spoolbear.model.request.UserUpdateRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.UpdateResponse;
import com.spoolbear.model.response.UserProfileDetailsResponse;
import com.spoolbear.model.response.UserProfileSidebarResponse;
import com.spoolbear.repository.UserProfileRepository;
import com.spoolbear.security.model.CustomUserDetails;
import com.spoolbear.security.model.User;
import com.spoolbear.service.CommonService;
import com.spoolbear.service.UserProfileService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.UserProfileValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final CommonService commonService;
    private final UserProfileValidationService userProfileValidationService;

    @Autowired
    public UserProfileServiceImpl(UserProfileRepository userProfileRepository,
                                  CommonService commonService,
                                  UserProfileValidationService userProfileValidationService) {
        this.userProfileRepository = userProfileRepository;
        this.commonService = commonService;
        this.userProfileValidationService = userProfileValidationService;
    }

    @Override
    public CommonResponse<UpdateResponse> updateUserProfileDetails(UserUpdateRequest userUpdateRequest) {
        LOGGER.info("Start updating user profile details.");
        try {
            UserProfileValidationService.validateUserUpdateRequest(userUpdateRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            Long addressId = userProfileRepository.getUserProfileAddressId(userId);
            if (addressId != null) {
                userUpdateRequest.setAddressId(addressId);
                userProfileRepository.updateUserProfileAddress(userUpdateRequest, addressId);
            }else{
                addressId = userProfileRepository.insertUserProfileAddress(new UserProfileAddressInsertRequest(
                        userUpdateRequest.getAddressNumber(),
                        userUpdateRequest.getAddressLine1(),
                        userUpdateRequest.getAddressLine2(),
                        userUpdateRequest.getCity(),
                        userUpdateRequest.getDistrict(),
                        userUpdateRequest.getProvince(),
                        userUpdateRequest.getCountry(),
                        userUpdateRequest.getPostalCode()
                ));
                userUpdateRequest.setAddressId(addressId);
            }
            LOGGER.info(userUpdateRequest.toString());
            userProfileRepository.updateUserProfileDetails(userUpdateRequest, userId);

            LOGGER.info("updated user profile details successfully");
            return (
                    new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                            new UpdateResponse("User profile details successfully updated", userId),
                            Instant.now()
                    )
            );
        } catch (DataNotFoundErrorExceptionHandler | UpdateFailedErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while updating user profile details: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to updating user profile details from database");
        } finally {
            LOGGER.info("End updating user profile details from repository");
        }
    }

    @Override
    public CommonResponse<List<UserProfileSidebarResponse>> getUserProfileSideBar() {
        LOGGER.info("Start fetching user profile side bar from repository");
        try {
            List<UserProfileSidebarResponse> userProfileSidebarResponses =
                    userProfileRepository.getUserProfileSideBar();

            LOGGER.info("Fetched user profile side bar successfully");


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authenticated user");
            }
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            User user = principal.getDomainUser();

            Set<String> userPrivileges = user.getPrivileges()
                    .stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            List<UserProfileSidebarResponse> userProfileSidebarResponseList = userProfileSidebarResponses
                    .stream()
                    .filter(data -> data.getStatus().equalsIgnoreCase(CommonStatus.ACTIVE.toString()))
                    .filter(data -> userPrivileges.contains(data.getPrivilegeName().toLowerCase()))
                    .toList();

            return (
                    new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                            userProfileSidebarResponseList,
                            Instant.now()
                    )
            );
        } catch (DataNotFoundErrorExceptionHandler e) {
            LOGGER.error("Error occurred while fetching user profile side bar: {}", e.getMessage(), e);
            throw new DataNotFoundErrorExceptionHandler(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching user profile side bar details: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch user profile side bar details from database");
        } finally {
            LOGGER.info("End fetching user profile side bar details from repository");
        }
    }

    @Override
    public CommonResponse<UserProfileDetailsResponse> getUserProfileDetails(
    ) {
        LOGGER.info("Start fetching user profile details from repository");
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            UserProfileDetailsResponse userProfileDetailsResponse =
                    userProfileRepository.getUserProfileDetails(new UserProfileDetailsRequest(userId));
            LOGGER.info("Fetched user profile details successfully");
            return (
                    new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                            userProfileDetailsResponse,
                            Instant.now()
                    )
            );
        } catch (DataNotFoundErrorExceptionHandler e) {
            LOGGER.error("Error occurred while fetching user profile details: {}", e.getMessage(), e);
            throw new DataNotFoundErrorExceptionHandler(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching user profile details: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch user profile details from database");
        } finally {
            LOGGER.info("End fetching user profile details from repository");
        }
    }
}
