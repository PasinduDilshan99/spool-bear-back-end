package com.printing._d.service;

import com.printing._d.model.request.ProductsFilterRequest;
import com.printing._d.model.response.CommonResponse;
import com.printing._d.model.response.ProductResponse;

import java.util.List;

public interface ProductsService {
    CommonResponse<List<ProductResponse>> getActiveProducts(ProductsFilterRequest productsFilterRequest);
}
