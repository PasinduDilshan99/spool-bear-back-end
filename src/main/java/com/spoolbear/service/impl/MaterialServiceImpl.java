package com.spoolbear.service.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.request.MaterialDetailsRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.MaterialDetailsResponse;
import com.spoolbear.repository.MaterialRepository;
import com.spoolbear.service.MaterialService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.MaterialValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialServiceImpl.class);

    private final MaterialRepository materialRepository;
    private final MaterialValidationService materialValidationService;

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository, MaterialValidationService materialValidationService) {
        this.materialRepository = materialRepository;
        this.materialValidationService = materialValidationService;
    }

    @Override
    public CommonResponse<List<MaterialDetailsResponse>> getAllMaterials() {
        LOGGER.info("Start fetching all materials from repository");
        try {
            List<MaterialDetailsResponse> materialDetailsResponses = materialRepository.getAllMaterials();

            if (materialDetailsResponses.isEmpty()) {
                LOGGER.warn("No materials found in database");
                throw new DataNotFoundErrorExceptionHandler("No materials found");
            }

            LOGGER.info("Fetched {} materials successfully", materialDetailsResponses.size());
            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    materialDetailsResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching materials: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch materials from database");
        } finally {
            LOGGER.info("End fetching all materials from repository");
        }
    }

    @Override
    public CommonResponse<MaterialDetailsResponse> getMaterialsDetailsById(MaterialDetailsRequest materialDetailsRequest) {
        LOGGER.info("Start fetching material details by id from repository");
        try {
            materialValidationService.validateMaterialDetailsRequest(materialDetailsRequest);
            MaterialDetailsResponse materialDetailsResponses = materialRepository.getMaterialsDetailsById(materialDetailsRequest);

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    materialDetailsResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching material details by id: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch material details by id from database");
        } finally {
            LOGGER.info("End fetching material details by id from repository");
        }
    }
}
