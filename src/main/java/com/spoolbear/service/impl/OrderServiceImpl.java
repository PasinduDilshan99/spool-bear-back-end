package com.spoolbear.service.impl;

import com.spoolbear.exception.DataAccessErrorExceptionHandler;
import com.spoolbear.exception.DataNotFoundErrorExceptionHandler;
import com.spoolbear.exception.InternalServerErrorExceptionHandler;
import com.spoolbear.model.dto.OrderInsertRequestDto;
import com.spoolbear.model.dto.OrderMainDetailsDto;
import com.spoolbear.model.dto.PrintingOrderInsertRequestDto;
import com.spoolbear.model.enums.CommonStatus;
import com.spoolbear.model.enums.OrderStatus;
import com.spoolbear.model.enums.OrderTypes;
import com.spoolbear.model.enums.PaymentStatus;
import com.spoolbear.model.request.DesignOrderInsertRequest;
import com.spoolbear.model.request.PrintingOrderInsertRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.OrderResponse;
import com.spoolbear.model.response.ProductResponse;
import com.spoolbear.repository.OrderRepository;
import com.spoolbear.service.CommonService;
import com.spoolbear.service.OrderService;
import com.spoolbear.util.CommonResponseMessages;
import com.spoolbear.validations.OrderValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CommonService commonService;
    private final OrderValidationService orderValidationService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CommonService commonService, OrderValidationService orderValidationService) {
        this.orderRepository = orderRepository;
        this.commonService = commonService;
        this.orderValidationService = orderValidationService;
    }

    @Override
    public CommonResponse<List<OrderResponse>> getOrderByuser() {
        LOGGER.info("Start fetching orders by user id from repository");
        try {
            List<OrderResponse> orderResponses = new ArrayList<>();

            Long userId = commonService.getUserIdBySecurityContext();
            List<OrderMainDetailsDto> orderMainDetailsDtoList = orderRepository.getOrderMainDetailsByUserId(userId);

            List<Long> productOrderIds = new ArrayList<>();
            List<Long> printingOrderIds = new ArrayList<>();

            for (OrderMainDetailsDto orderMainDetailsDto : orderMainDetailsDtoList) {
                if (orderMainDetailsDto.getOrderType().equalsIgnoreCase("PRINTING")) {
                    printingOrderIds.add(orderMainDetailsDto.getOrderId());
                } else if (orderMainDetailsDto.getOrderType().equalsIgnoreCase("PRODUCT")) {
                    productOrderIds.add(orderMainDetailsDto.getOrderId());
                } else {
                    throw new InternalServerErrorExceptionHandler("Invalid Order Type");
                }
            }

            List<OrderResponse.printings> printingsList = orderRepository.getPrintingsDetailsByOrderIdList(printingOrderIds);
            List<OrderResponse.products> productsList = orderRepository.getProductsDetailsByOrderIdList(productOrderIds);

            for (OrderMainDetailsDto orderMainDetailsDto : orderMainDetailsDtoList) {
                OrderResponse orderResponse = new OrderResponse();
                if (orderMainDetailsDto.getOrderType().equalsIgnoreCase("PRINTING")) {
                    orderResponse = OrderResponse.builder()
                            .orderId(orderMainDetailsDto.getOrderId())
                            .userId(orderMainDetailsDto.getUserId())
                            .totalAmount(orderMainDetailsDto.getTotalAmount())
                            .orderStatus(orderMainDetailsDto.getOrderStatus())
                            .paymentStatus(orderMainDetailsDto.getPaymentStatus())
                            .status(orderMainDetailsDto.getStatus())
                            .createdDate(orderMainDetailsDto.getCreatedDate())
                            .updatedDate(orderMainDetailsDto.getUpdatedDate())
                            .orderType(orderMainDetailsDto.getOrderType())
                            .orderItems(OrderResponse.OrderItems.builder().build())
                            .build();

                    for (OrderResponse.printings printings : printingsList) {
                        if (printings.getOrderId().equals(orderMainDetailsDto.getOrderId())) {
                            orderResponse.getOrderItems().getPrintingsList().add(printings);
                        }
                    }

                } else if (orderMainDetailsDto.getOrderType().equalsIgnoreCase("PRODUCT")) {
                    orderResponse = OrderResponse.builder()
                            .orderId(orderMainDetailsDto.getOrderId())
                            .userId(orderMainDetailsDto.getUserId())
                            .totalAmount(orderMainDetailsDto.getTotalAmount())
                            .orderStatus(orderMainDetailsDto.getOrderStatus())
                            .paymentStatus(orderMainDetailsDto.getPaymentStatus())
                            .status(orderMainDetailsDto.getStatus())
                            .createdDate(orderMainDetailsDto.getCreatedDate())
                            .updatedDate(orderMainDetailsDto.getUpdatedDate())
                            .orderType(orderMainDetailsDto.getOrderType())
                            .orderItems(OrderResponse.OrderItems.builder().build())
                            .build();

                    for (OrderResponse.products products : productsList) {
                        if (products.getProductId().equals(orderMainDetailsDto.getOrderId())) {
                            orderResponse.getOrderItems().getProductsList().add(products);
                        }
                    }
                }
                orderResponses.add(orderResponse);
            }

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_CODE,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_RETRIEVE_MESSAGE,
                    orderResponses,
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while fetching orders by user id: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to fetch orders by user id from database");
        } finally {
            LOGGER.info("End fetching orders by user id from repository");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addPrintingOrder(PrintingOrderInsertRequest printingOrderInsertRequest) {
        LOGGER.info("Start add printing order.");
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            orderValidationService.validatePrintingOrderRequest(printingOrderInsertRequest);
            Long orderId = orderRepository.addOrder(
                    new OrderInsertRequestDto(
                            userId,
                            null,
                            OrderStatus.UNDER_REVIEW.toString(),
                            1,
                            OrderTypes.PRINTING.toString(),
                            PaymentStatus.PENDING.toString()
                    ));
            Long printOrderId = orderRepository.addPrintingOrder(
                    new PrintingOrderInsertRequestDto(
                            orderId,
                            null,
                            printingOrderInsertRequest.getCustomText(),
                            printingOrderInsertRequest.getDescription(),
                            printingOrderInsertRequest.getSize(),
                            printingOrderInsertRequest.getColor(),
                            printingOrderInsertRequest.getQuantity(),
                            OrderStatus.UNDER_REVIEW.toString(),
                            printingOrderInsertRequest.getMateriel(),
                            userId
                    ));
            for (PrintingOrderInsertRequest.OrderFile orderFile : printingOrderInsertRequest.getOrderFiles()) {
                orderRepository.addPrintingOrderFile(orderFile.getFileName(), orderFile.getFileUrl(), printOrderId, userId);
            }

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    new InsertResponse("Printing Order Added Successfully"),
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while add printing order: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to add printing order");
        } finally {
            LOGGER.info("End add printing order");
        }
    }

    @Override
    public CommonResponse<InsertResponse> addDesignOrder(DesignOrderInsertRequest designOrderInsertRequest) {
        LOGGER.info("Start add design order.");
        try {
            Long userId = commonService.getUserIdBySecurityContext();
            orderValidationService.validateDesignOrderInsertRequest(designOrderInsertRequest);
            Long orderId = orderRepository.addOrder(
                    new OrderInsertRequestDto(
                            userId,
                            null,
                            OrderStatus.UNDER_REVIEW.toString(),
                            1,
                            OrderTypes.PRINTING.toString(),
                            PaymentStatus.PENDING.toString()
                    ));
            Long printOrderId = orderRepository.addPrintingOrder(
                    new PrintingOrderInsertRequestDto(
                            orderId,
                            null,
                            designOrderInsertRequest.getCustomText(),
                            designOrderInsertRequest.getDescription(),
                            designOrderInsertRequest.getSize(),
                            designOrderInsertRequest.getColor(),
                            designOrderInsertRequest.getQuantity(),
                            OrderStatus.REQUESTED.toString(),
                            designOrderInsertRequest.getMateriel(),
                            userId
                    ));

            return new CommonResponse<>(
                    CommonResponseMessages.SUCCESSFULLY_INSERT_CODE,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_STATUS,
                    CommonResponseMessages.SUCCESSFULLY_INSERT_MESSAGE,
                    new InsertResponse("design Order Added Successfully"),
                    Instant.now());

        } catch (DataNotFoundErrorExceptionHandler | DataAccessErrorExceptionHandler e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error occurred while add design order: {}", e.getMessage(), e);
            throw new InternalServerErrorExceptionHandler("Failed to add design order");
        } finally {
            LOGGER.info("End add design order");
        }
    }
}
