package com.spoolbear.service.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.InsertInquiryRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.GalleryResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.repository.ContactUsRepository;
import com.spoolbear.service.ContactUsService;
import com.spoolbear.util.CommonResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ContactUsServiceImpl implements ContactUsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsServiceImpl.class);

    private final ContactUsRepository contactUsRepository;

    @Autowired
    public ContactUsServiceImpl(ContactUsRepository contactUsRepository) {
        this.contactUsRepository = contactUsRepository;
    }

    @Override
    public CommonResponse<InsertResponse> addInquiry(InsertInquiryRequest insertInquiryRequest) {
        LOGGER.info("Start add inquiry");

        try {
            boolean isAdded = contactUsRepository.addInquiry(insertInquiryRequest);
            if (!isAdded) {
                throw new InternalServerErrorExceptionHandler("Failed to add inquiry to database");
            }
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    new InsertResponse("Successfully added the inquiry"),
                    Instant.now());

        }catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        }catch (Exception e) {
            LOGGER.error("Error occurred while add inquiry: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to add inquiry");
        } finally {
            LOGGER.info("End add inquiry");
        }
    }
}
