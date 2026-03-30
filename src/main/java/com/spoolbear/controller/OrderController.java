package com.spoolbear.controller;

import com.spoolbear.model.request.DesignOrderInsertRequest;
import com.spoolbear.model.request.PrintingOrderInsertRequest;
import com.spoolbear.model.request.ProductOrderInsertRequest;
import com.spoolbear.model.request.ProductsFilterRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.OrderResponse;
import com.spoolbear.model.response.ProductResponse;
import com.spoolbear.service.OrderService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(path = "/orders-by-user")
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getOrderByuser() {
        LOGGER.info("{} Start execute get orders by user id {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<OrderResponse>> response = orderService.getOrderByuser();
        LOGGER.info("{} End execute get orders by user id  {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-product-order")
    public ResponseEntity<CommonResponse<InsertResponse>> addProductOrder(@RequestBody ProductOrderInsertRequest productOrderInsertRequest) {
        LOGGER.info("{} Start execute add product order  {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = orderService.addProductOrder(productOrderInsertRequest);
        LOGGER.info("{} End execute add product order {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-printing-order")
    public ResponseEntity<CommonResponse<InsertResponse>> addPrintingOrder(@RequestBody PrintingOrderInsertRequest printingOrderInsertRequest) {
        LOGGER.info("{} Start execute add printing order  {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = orderService.addPrintingOrder(printingOrderInsertRequest);
        LOGGER.info("{} End execute add printing order {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/add-design-order")
    public ResponseEntity<CommonResponse<InsertResponse>> addDesignOrder(@RequestBody DesignOrderInsertRequest designOrderInsertRequest) {
        LOGGER.info("{} Start execute add design order  {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = orderService.addDesignOrder(designOrderInsertRequest);
        LOGGER.info("{} End execute add design order {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
