package com.spoolbear.service;

import com.spoolbear.model.request.DesignOrderInsertRequest;
import com.spoolbear.model.request.PrintingOrderInsertRequest;
import com.spoolbear.model.request.ProductOrderInsertRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.OrderResponse;

import java.util.List;

public interface OrderService {
    CommonResponse<List<OrderResponse>> getOrderByuser();

    CommonResponse<InsertResponse> addPrintingOrder(PrintingOrderInsertRequest printingOrderInsertRequest);

    CommonResponse<InsertResponse> addDesignOrder(DesignOrderInsertRequest designOrderInsertRequest);

    CommonResponse<InsertResponse> addProductOrder(ProductOrderInsertRequest productOrderInsertRequest);
}
