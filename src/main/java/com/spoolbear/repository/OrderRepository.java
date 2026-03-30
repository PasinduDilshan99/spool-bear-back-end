package com.spoolbear.repository;

import com.spoolbear.model.dto.OrderInsertRequestDto;
import com.spoolbear.model.dto.OrderMainDetailsDto;
import com.spoolbear.model.dto.PrintingOrderInsertRequestDto;
import com.spoolbear.model.request.ProductOrderInsertRequest;
import com.spoolbear.model.response.OrderResponse;

import java.util.List;

public interface OrderRepository {
    List<OrderMainDetailsDto> getOrderMainDetailsByUserId(Long userId);

    List<OrderResponse.printings> getPrintingsDetailsByOrderIdList(List<Long> printingOrderIds);

    List<OrderResponse.products> getProductsDetailsByOrderIdList(List<Long> productOrderIds);

    Long addPrintingOrder(PrintingOrderInsertRequestDto printingOrderInsertRequestDto);

    void addPrintingOrderFile(String fileName, String fileUrl, Long printOrderId, Long userId);

    Long addOrder(OrderInsertRequestDto orderInsertRequestDto);

    boolean addProductOrder(ProductOrderInsertRequest.OrderProducts orderProducts, Long orderId, Long userId);
}
