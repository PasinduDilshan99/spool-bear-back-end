package com.spoolbear.repository;

import com.spoolbear.model.request.ProductDetailsRequest;
import com.spoolbear.model.request.ProductsFilterRequest;
import com.spoolbear.model.response.ProductResponse;

import java.util.List;

public interface ProductsRepository {
    List<ProductResponse> getActiveProducts(ProductsFilterRequest productsFilterRequest);

    List<Long> getWishListProductIdsByUserId(Long userId);

    ProductResponse getProductDetailsById(ProductDetailsRequest productDetailsRequest);
}
