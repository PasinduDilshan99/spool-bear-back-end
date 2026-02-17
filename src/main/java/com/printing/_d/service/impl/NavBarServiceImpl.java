package com.printing._d.service.impl;

import com.printing._d.exception.DataAccessErrorExceptionHandler;
import com.printing._d.exception.DataNotFoundErrorExceptionHandler;
import com.printing._d.exception.InternalServerErrorExceptionHandler;
import com.printing._d.model.response.CommonResponse;
import com.printing._d.model.response.NavBarResponse;
import com.printing._d.repository.NavBarRepository;
import com.printing._d.service.NavBarService;
import com.printing._d.util.CommonResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NavBarServiceImpl implements NavBarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavBarServiceImpl.class);

    private final NavBarRepository navBarRepository;

    @Autowired
    public NavBarServiceImpl(NavBarRepository navBarRepository) {
        this.navBarRepository = navBarRepository;
    }

    @Override
    public CommonResponse<List<NavBarResponse>> getActiveNavBarData() {
        LOGGER.info("Start fetching active nav bar data from repository");

        try {
            List<NavBarResponse> navBarResponses = navBarRepository.getActiveNavBarData();

            if (navBarResponses.isEmpty()) {
                LOGGER.warn("No active nav bar data found in database");
                throw new DataNotFoundErrorExceptionHandler("No active nav bar data found");
            }

            LOGGER.info("Fetched {} active nav bar data successfully", navBarResponses.size());

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    navBarResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching active nav bar data: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch active nav bar data from database");
        } finally {
            LOGGER.info("End fetching active nav bar data from repository");
        }
    }

}
