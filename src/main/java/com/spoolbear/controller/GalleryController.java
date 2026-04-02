package com.spoolbear.controller;

import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.GalleryResponse;
import com.spoolbear.service.GalleryService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/gallery")
public class GalleryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GalleryController.class);

    private final GalleryService galleryService;

    @Autowired
    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @GetMapping(path = "/active")
    public ResponseEntity<CommonResponse<List<GalleryResponse>>> getActiveImages(){
        LOGGER.info("{} Start execute get active images {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<GalleryResponse>> response = galleryService.getActiveImages();
        LOGGER.info("{} End execute get active images {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
