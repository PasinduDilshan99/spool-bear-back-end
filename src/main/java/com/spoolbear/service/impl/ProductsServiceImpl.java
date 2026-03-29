package com.spoolbear.service.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.ProductDetailsRequest;
import com.spoolbear.model.request.ProductsFilterRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.ProductResponse;
import com.spoolbear.repository.ProductsRepository;
import com.spoolbear.service.CommonService;
import com.spoolbear.service.ProductsService;
import com.spoolbear.util.CommonResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProductsServiceImpl implements ProductsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsServiceImpl.class);

    private final ProductsRepository productsRepository;
    private final CommonService commonService;

    @Autowired
    public ProductsServiceImpl(ProductsRepository productsRepository, CommonService commonService) {
        this.productsRepository = productsRepository;
        this.commonService = commonService;
    }

    @Override
    public CommonResponse<List<ProductResponse>> getActiveProducts(ProductsFilterRequest productsFilterRequest) {
        LOGGER.info("Start fetching active products from repository");
        try {
            Long userId = null;
            userId = commonService.getUserIdBySecurityContextWithOutException();
            List<ProductResponse> productResponses = productsRepository.getActiveProducts(productsFilterRequest);
            if (userId != null) {
                List<Long> wishProductIdList = productsRepository.getWishListProductIdsByUserId(userId);
                productResponses.forEach(productResponse -> productResponse.setIsWish(wishProductIdList.contains(productResponse.getProductId())));
            }


            if (productResponses.isEmpty()) {
                LOGGER.warn("No active products found in database");
                throw new DataNotFoundErrorExceptionHandler("No active products found");
            }

            LOGGER.info("Fetched {} active products successfully", productResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    productResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching active products: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch active products from database");
        } finally {
            LOGGER.info("End fetching active products from repository");
        }
    }

    @Override
    public CommonResponse<ProductResponse> getProductDetailsById(ProductDetailsRequest productDetailsRequest) {
        LOGGER.info("Start fetching product details by id from repository");
        try {
            Long userId = null;
            userId = commonService.getUserIdBySecurityContextWithOutException();
            ProductResponse productResponses = productsRepository.getProductDetailsById(productDetailsRequest);
            if (userId != null) {
                List<Long> wishProductIdList = productsRepository.getWishListProductIdsByUserId(userId);
                    productResponses.setIsWish(wishProductIdList.contains(productResponses.getProductId()));
            }

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    productResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching active products: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch active products from database");
        } finally {
            LOGGER.info("End fetching active products from repository");
        }
    }
}
