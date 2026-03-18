package com.spoolbear.service.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.GalleryResponse;
import com.spoolbear.repository.GalleryRepository;
import com.spoolbear.service.GalleryService;
import com.spoolbear.util.CommonResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GalleryServiceImpl implements GalleryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GalleryServiceImpl.class);

    private final GalleryRepository galleryRepository;

    @Autowired
    public GalleryServiceImpl(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    @Override
    public CommonResponse<List<GalleryResponse>> getActiveImages() {
        LOGGER.info("Start fetching active images from repository");

        try {
            List<GalleryResponse> galleryResponses = galleryRepository.getActiveImages();

            if (galleryResponses.isEmpty()) {
                LOGGER.warn("No active images found in database");
                throw new DataNotFoundErrorExceptionHandler("No active images found");
            }

            LOGGER.info("Fetched {} active images successfully", galleryResponses.size());

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    galleryResponses,
                    Instant.now());

        }catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        }catch (Exception e) {
            LOGGER.error("Error occurred while fetching active images: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch active images from database");
        } finally {
            LOGGER.info("End fetching active images from repository");
        }
    }

}
