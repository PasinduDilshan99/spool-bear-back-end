package com.spoolbear.service;

import com.spoolbear.model.request.FetchCartRequest;
import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.request.RemoveAllFromCartRequest;
import com.spoolbear.model.request.RemoveItemFromCartRequest;
import com.spoolbear.model.response.CartCreateResponse;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.ProductsCartResponse;
import com.spoolbear.model.response.RemoveAllProductsResponse;

import java.util.List;

public interface CartService {
    CommonResponse<CartCreateResponse> createCart(InsertItemToCartRequest insertItemToCartRequest);

    CommonResponse<List<ProductsCartResponse>> fetchCart(FetchCartRequest fetchCartRequest);

    CommonResponse<List<ProductsCartResponse>> addProductToCart(InsertItemToCartRequest insertItemToCartRequest);

    CommonResponse<List<ProductsCartResponse>> removeProductFromCart(RemoveItemFromCartRequest removeItemFromCartRequest);

    CommonResponse<RemoveAllProductsResponse> removeAllFromCart(RemoveAllFromCartRequest removeAllFromCartRequest);
}
