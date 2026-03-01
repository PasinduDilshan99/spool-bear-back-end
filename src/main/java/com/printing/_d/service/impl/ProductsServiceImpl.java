package com.printing._d.service.impl;

import com.printing._d.exception.DataAccessErrorExceptionHandler;
import com.printing._d.exception.DataNotFoundErrorExceptionHandler;
import com.printing._d.exception.InternalServerErrorExceptionHandler;
import com.printing._d.model.request.ProductsFilterRequest;
import com.printing._d.model.response.BlogResponse;
import com.printing._d.model.response.CommonResponse;
import com.printing._d.model.response.ProductResponse;
import com.printing._d.repository.ProductsRepository;
import com.printing._d.service.ProductsService;
import com.printing._d.util.CommonResponseMessages;
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

    @Autowired
    public ProductsServiceImpl(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @Override
    public CommonResponse<List<ProductResponse>> getActiveProducts(ProductsFilterRequest productsFilterRequest) {
        LOGGER.info("Start fetching active products from repository");
        try {
            List<ProductResponse> productResponses = productsRepository.getActiveProducts(productsFilterRequest);

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
}
