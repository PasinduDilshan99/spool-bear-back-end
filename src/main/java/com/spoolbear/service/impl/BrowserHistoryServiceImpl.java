package com.spoolbear.service.impl;

import com.spoolbear.exception.*;
import com.spoolbear.model.request.BrowsingHistoryRequest;
import com.spoolbear.model.request.InsertBrowserHistoryRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryListRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryRequest;
import com.spoolbear.model.response.BrowserHistoryResponse;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.UpdateResponse;
import com.spoolbear.repository.BrowserHistoryRepository;
import com.spoolbear.service.BrowserHistoryService;
import com.spoolbear.service.CommonService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.BrowserHistoryValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BrowserHistoryServiceImpl implements BrowserHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserHistoryServiceImpl.class);

    private final BrowserHistoryRepository browserHistoryRepository;
    private final BrowserHistoryValidationService browserHistoryValidationService;
    private final CommonService commonService;

    @Autowired
    public BrowserHistoryServiceImpl(BrowserHistoryRepository browserHistoryRepository, BrowserHistoryValidationService browserHistoryValidationService, CommonService commonService) {
        this.browserHistoryRepository = browserHistoryRepository;
        this.browserHistoryValidationService = browserHistoryValidationService;
        this.commonService = commonService;
    }

    @Override
    public CommonResponse<InsertResponse> addHistoryData(InsertBrowserHistoryRequest insertBrowserHistoryRequest) {
        try {
            browserHistoryValidationService.validateInsertBrowserHistoryRequest(insertBrowserHistoryRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            browserHistoryRepository.addHistoryData(insertBrowserHistoryRequest, userId);

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    new InsertResponse("Successfully insert browser history request"),
                    Instant.now());

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert browser history request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<BrowserHistoryResponse> getHistoryData(BrowsingHistoryRequest browsingHistoryRequest) {
        LOGGER.info("Start fetching browser history data from repository");

        try {
            Long userId = commonService.getUserIdBySecurityContext();
            BrowserHistoryResponse browserHistoryResponses =
                    browserHistoryRepository.getHistoryData(userId, browsingHistoryRequest);

            if (browserHistoryResponses == null) {
                LOGGER.warn("No browser history data found in database");
                throw new DataNotFoundErrorExceptionHandler("No browser history data found");
            }

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    browserHistoryResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler e) {
            LOGGER.error("Error occurred while fetching active history data: {}", e.getMessage(), e);
            throw new DataNotFoundErrorExceptionHandler(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching active history data: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to active history data from database");
        } finally {
            LOGGER.info("End fetching active history data from repository");
        }
    }

    @Override
    public CommonResponse<UpdateResponse> removeHistoryData(RemoveBrowserHistoryRequest removeBrowserHistoryRequest) {
        try {
            browserHistoryValidationService.validateRemoveBrowserHistoryRequest(removeBrowserHistoryRequest);
            browserHistoryRepository.removeHistoryData(removeBrowserHistoryRequest);

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_MESSAGE,
                    new UpdateResponse("Successfully remove browser history request", removeBrowserHistoryRequest.getHistoryDataId()),
                    Instant.now());

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the remove browser history request", vfe.getValidationFailedResponses());
        } catch (UpdateFailedErrorExceptionHandler ufe) {
            throw new UpdateFailedErrorExceptionHandler(ufe.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<UpdateResponse> removeAllHistoryData() {
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            browserHistoryRepository.removeAllHistoryData(userId);

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_MESSAGE,
                    new UpdateResponse("Successfully remove all browser history request", null),
                    Instant.now());

        } catch (UpdateFailedErrorExceptionHandler ufe) {
            throw new UpdateFailedErrorExceptionHandler(ufe.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

    @Override
    public CommonResponse<UpdateResponse> removeHistoryDataList(RemoveBrowserHistoryListRequest removeBrowserHistoryListRequest) {
        try {
            browserHistoryValidationService.validateRemoveBrowserHistoryListRequest(removeBrowserHistoryListRequest);
            browserHistoryRepository.removeHistoryDataList(removeBrowserHistoryListRequest);

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_REMOVE_MESSAGE,
                    new UpdateResponse("Successfully remove browser history request list", null),
                    Instant.now());

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the remove browser history request list", vfe.getValidationFailedResponses());
        } catch (UpdateFailedErrorExceptionHandler ufe) {
            throw new UpdateFailedErrorExceptionHandler(ufe.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }


}
