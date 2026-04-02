package com.spoolbear.service.impl;

import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InsertFailedErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.exception.ValidationFailedErrorExceptionHandler;
import com.spoolbear.model.request.UpdateUserNotificationPermissionRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.UpdateResponse;
import com.spoolbear.model.response.UserNotificationPermissionResponse;
import com.spoolbear.repository.UserNotificationPermissionRepository;
import com.spoolbear.service.CommonService;
import com.spoolbear.service.UserNotificationPermissionService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.UserNotificationPermissionValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserNotificationPermissionServiceImpl implements UserNotificationPermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationPermissionServiceImpl.class);

    private final UserNotificationPermissionRepository userNotificationPermissionRepository;
    private final CommonService commonService;
    private final UserNotificationPermissionValidationService userNotificationPermissionValidationService;

    @Autowired
    public UserNotificationPermissionServiceImpl(UserNotificationPermissionRepository userNotificationPermissionRepository,
                                                 CommonService commonService,
                                                 UserNotificationPermissionValidationService userNotificationPermissionValidationService) {
        this.userNotificationPermissionRepository = userNotificationPermissionRepository;
        this.commonService=commonService;
        this.userNotificationPermissionValidationService = userNotificationPermissionValidationService;
    }

    @Override
    public CommonResponse<UserNotificationPermissionResponse> getUserNotificationPermissionById() {
        LOGGER.info("Start fetching user notification permission details from repository");
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            UserNotificationPermissionResponse userNotificationPermissionResponse =
                    userNotificationPermissionRepository.getUserNotificationPermissionById(userId);
            LOGGER.info("Fetched user notification permission details successfully");
            return (
                    new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                            userNotificationPermissionResponse,
                            Instant.now()
                    )
            );
        } catch (DataNotFoundErrorExceptionHandler e) {
            LOGGER.error("Error occurred while fetching user notification permission details: {}", e.getMessage(), e);
            throw new DataNotFoundErrorExceptionHandler(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching user notification permission details: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch user notification permission details from database");
        } finally {
            LOGGER.info("End fetching user notification permission details from repository");
        }
    }

    @Override
    public CommonResponse<UpdateResponse> updateUserNotificationPermissionDetails(UpdateUserNotificationPermissionRequest updateUserNotificationPermissionRequest) {
        try {
            userNotificationPermissionValidationService.validateUpdateUserNotificationPermissionRequest(updateUserNotificationPermissionRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            userNotificationPermissionRepository.updateUserNotificationPermissionDetails(updateUserNotificationPermissionRequest,userId);
            return
                    new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                            new UpdateResponse("Successfully update update user notification permission request", null),
                            Instant.now()
                    );
        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the update user notification permission request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());

        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

}
