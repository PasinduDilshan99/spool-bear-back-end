package com.spoolbear.controller;

import com.spoolbear.model.request.WishListInsertRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.WishListResponse;
import com.spoolbear.service.WishListService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/wish-list")
public class WishListController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishListController.class);

    private final WishListService wishListService;

    @Autowired
    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @GetMapping(path = "/details")
    public ResponseEntity<CommonResponse<List<WishListResponse>>> getWishListDetails() {
        LOGGER.info("{} Start execute get user wish list details {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<WishListResponse>> response = wishListService.getWishListDetails();
        LOGGER.info("{} End execute get user wish list details {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-wish-list")
    public ResponseEntity<CommonResponse<InsertResponse>> addAWishList(@RequestBody WishListInsertRequest wishListInsertRequest) {
        LOGGER.info("{} Start execute add wish list data {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = wishListService.addAWishList(wishListInsertRequest);
        LOGGER.info("{} End execute add wish list data {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
