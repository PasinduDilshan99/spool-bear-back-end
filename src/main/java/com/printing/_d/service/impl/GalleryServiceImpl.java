package com.printing._d.service.impl;

import com.printing._d.exception.DataAccessErrorExceptionHandler;
import com.printing._d.exception.DataNotFoundErrorExceptionHandler;
import com.printing._d.exception.InternalServerErrorExceptionHandler;
import com.printing._d.model.response.CommonResponse;
import com.printing._d.model.response.GalleryResponse;
import com.printing._d.repository.GalleryRepository;
import com.printing._d.service.GalleryService;
import com.printing._d.util.CommonResponseMessages;
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
