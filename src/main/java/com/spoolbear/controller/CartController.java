package com.spoolbear.controller;

import com.spoolbear.model.request.FetchCartRequest;
import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.request.RemoveAllFromCartRequest;
import com.spoolbear.model.request.RemoveItemFromCartRequest;
import com.spoolbear.model.response.*;
import com.spoolbear.service.CartService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/cart")
public class CartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<CommonResponse<CartCreateResponse>> createCart(@RequestBody InsertItemToCartRequest insertItemToCartRequest) {
        LOGGER.info("{} Start execute create cart {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<CartCreateResponse> response = cartService.createCart(insertItemToCartRequest);
        LOGGER.info("{} End execute create cart {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/fetch")
    public ResponseEntity<CommonResponse<List<ProductsCartResponse>>> fetchCart(@RequestBody FetchCartRequest fetchCartRequest) {
        LOGGER.info("{} Start execute fetch cart {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<ProductsCartResponse>> response = cartService.fetchCart(fetchCartRequest);
        LOGGER.info("{} End execute fetch cart {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-product")
    public ResponseEntity<CommonResponse<List<ProductsCartResponse>>> addProductToCart(@RequestBody InsertItemToCartRequest insertItemToCartRequest) {
        LOGGER.info("{} Start execute add product to cart {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<ProductsCartResponse>> response = cartService.addProductToCart(insertItemToCartRequest);
        LOGGER.info("{} End execute add product to cart {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/remove-product")
    public ResponseEntity<CommonResponse<List<ProductsCartResponse>>> removeProductFromCart(@RequestBody RemoveItemFromCartRequest removeItemFromCartRequest) {
        LOGGER.info("{} Start execute remove product from cart {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<ProductsCartResponse>> response = cartService.removeProductFromCart(removeItemFromCartRequest);
        LOGGER.info("{} End execute remove product from cart {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/remove-all-product")
    public ResponseEntity<CommonResponse<RemoveAllProductsResponse>> removeAllFromCart(@RequestBody RemoveAllFromCartRequest removeAllFromCartRequest) {
        LOGGER.info("{} Start execute remove all from cart {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<RemoveAllProductsResponse> response = cartService.removeAllFromCart(removeAllFromCartRequest);
        LOGGER.info("{} End execute remove all from cart {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
