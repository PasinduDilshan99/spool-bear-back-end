package com.spoolbear.service;

import com.spoolbear.model.request.ProductDetailsRequest;
import com.spoolbear.model.request.ProductsFilterRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.ProductResponse;

import java.util.List;

public interface ProductsService {
    CommonResponse<List<ProductResponse>> getActiveProducts(ProductsFilterRequest productsFilterRequest);

    CommonResponse<ProductResponse> getProductDetailsById(ProductDetailsRequest productDetailsRequest);
}
