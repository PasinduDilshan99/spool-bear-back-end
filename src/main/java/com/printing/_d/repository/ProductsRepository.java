package com.printing._d.repository;

import com.printing._d.model.request.ProductsFilterRequest;
import com.printing._d.model.response.ProductResponse;

import java.util.List;

public interface ProductsRepository {
    List<ProductResponse> getActiveProducts(ProductsFilterRequest productsFilterRequest);
}
