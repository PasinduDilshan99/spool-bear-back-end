package com.spoolbear.controller;

import com.spoolbear.model.request.MaterialDetailsRequest;
import com.spoolbear.model.response.BlogResponse;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.MaterialDetailsResponse;
import com.spoolbear.service.MaterialService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/material")
public class MaterialController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialController.class);

    private final MaterialService materialService;

    @Autowired
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<CommonResponse<List<MaterialDetailsResponse>>> getAllMaterials() {
        LOGGER.info("{} Start execute get all materials {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<MaterialDetailsResponse>> materials = materialService.getAllMaterials();
        LOGGER.info("{} End execute get all materials {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(materials, HttpStatus.OK);
    }

    @PostMapping(path = "/material-details-by-id")
    public ResponseEntity<CommonResponse<MaterialDetailsResponse>> getMaterialsDetailsById(@RequestBody MaterialDetailsRequest materialDetailsRequest) {
        LOGGER.info("{} Start execute get material details by id {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<MaterialDetailsResponse> materialDetails = materialService.getMaterialsDetailsById(materialDetailsRequest);
        LOGGER.info("{} End execute get material details by id {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(materialDetails, HttpStatus.OK);
    }

}
