package com.spoolbear.controller;

import com.spoolbear.model.request.ProductDetailsRequest;
import com.spoolbear.model.request.ProductsFilterRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.ProductResponse;
import com.spoolbear.service.ProductsService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/products")
public class ProductsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);

    private final ProductsService productsService;

    @Autowired
    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @PostMapping(path = "/active-products")
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getActiveProducts(@RequestBody ProductsFilterRequest productsFilterRequest) {
        LOGGER.info("{} Start execute get active products {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<ProductResponse>> response = productsService.getActiveProducts(productsFilterRequest);
        LOGGER.info("{} End execute get active products {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/product-details")
    public ResponseEntity<CommonResponse<ProductResponse>> getProductDetailsById(@RequestBody ProductDetailsRequest productDetailsRequest) {
        LOGGER.info("{} Start execute get product details by id {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<ProductResponse> response = productsService.getProductDetailsById(productDetailsRequest);
        LOGGER.info("{} End execute get product details by id {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
