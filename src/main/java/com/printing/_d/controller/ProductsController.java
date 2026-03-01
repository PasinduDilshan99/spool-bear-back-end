package com.printing._d.controller;

import com.printing._d.model.request.ProductsFilterRequest;
import com.printing._d.model.response.BlogResponse;
import com.printing._d.model.response.CommonResponse;
import com.printing._d.model.response.ProductResponse;
import com.printing._d.service.ProductsService;
import com.printing._d.util.Constant;
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


}
