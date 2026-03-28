package com.spoolbear.service.impl;

import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InsertFailedErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.exception.ValidationFailedErrorExceptionHandler;
import com.spoolbear.model.dto.ExistWishListDataDto;
import com.spoolbear.model.request.WishListInsertRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.WishListResponse;
import com.spoolbear.repository.WishListRepository;
import com.spoolbear.service.CommonService;
import com.spoolbear.service.WishListService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.WishListValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class WishListServiceImpl implements WishListService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishListServiceImpl.class);

    private final WishListRepository wishListRepository;
    private final CommonService commonService;
    private final WishListValidationService wishListValidationService;

    @Autowired
    public WishListServiceImpl(WishListRepository wishListRepository, CommonService commonService, WishListValidationService wishListValidationService) {
        this.wishListRepository = wishListRepository;
        this.commonService = commonService;
        this.wishListValidationService = wishListValidationService;
    }

    @Override
    public CommonResponse<List<WishListResponse>> getWishListDetails() {
        LOGGER.info("Start fetching wish list details from repository");
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            List<WishListResponse> wishlistItemResponse = wishListRepository.getWishListDetails(userId);
            return new CommonResponse<>(
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                            CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                            wishlistItemResponse,
                            Instant.now());
        } catch (DataNotFoundErrorExceptionHandler e) {
            LOGGER.error("Error occurred while fetching user account security details: {}", e.getMessage(), e);
            throw new DataNotFoundErrorExceptionHandler(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching user account security details: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch user account security details from database");
        } finally {
            LOGGER.info("End fetching user account security details from repository");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addAWishList(WishListInsertRequest wishListInsertRequest) {
        try {
            wishListValidationService.validateWishListInsertRequest(wishListInsertRequest);
            Long userId = commonService.getUserIdBySecurityContext();
            ExistWishListDataDto existActivityWishListDataDto = wishListRepository.getExistingWishListData(userId, wishListInsertRequest);
            if (existActivityWishListDataDto == null) {
                Long wishListId = wishListRepository.addWishList(wishListInsertRequest, userId);
                wishListRepository.addWishListHistory(wishListInsertRequest, userId, wishListId, "ACTIVE");
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                        new InsertResponse("Successfully insert wish list request"),
                        Instant.now());
            } else {
                wishListRepository.updateWishList(wishListInsertRequest, userId, existActivityWishListDataDto);
                wishListRepository.addWishListHistory(wishListInsertRequest, userId, existActivityWishListDataDto.getWishListId(), existActivityWishListDataDto.getStatus());
                return new CommonResponse<>(
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_CODE,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_STATUS,
                        CommonResponseMessages.SUCCESSFULLY_UPDATE_MESSAGE,
                        new InsertResponse("Successfully update wish list request"),
                        Instant.now());
            }

        } catch (ValidationFailedErrorExceptionHandler vfe) {
            throw new ValidationFailedErrorExceptionHandler("validation failed in the insert wish list request", vfe.getValidationFailedResponses());
        } catch (InsertFailedErrorExceptionHandler ife) {
            throw new InsertFailedErrorExceptionHandler(ife.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString());
            throw new InternalServerErrorExceptionHandler("Something went wrong");
        }
    }

}
