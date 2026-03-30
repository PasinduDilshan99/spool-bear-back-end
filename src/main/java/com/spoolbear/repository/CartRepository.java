package com.spoolbear.repository;

import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.request.RemoveItemFromCartRequest;
import com.spoolbear.model.response.ProductsCartResponse;

import java.util.List;

public interface CartRepository {
    Long createCart(Long userId);

    List<ProductsCartResponse> fetchCart(Long cartId);

    boolean addProductToCart(InsertItemToCartRequest insertItemToCartRequest);

    void decreaseProductQuantityByOne(Long productId);

    int getCurrentQuantity(Long cartItemId);

    void decreaseCartProductQuantityByOne(Long cartItemId);

    void increaseProductQuantityByOne(Long productId);

    void removeProductFromCart(Long cartItemId);

    boolean increaseCartProductQuantityByOne(Long cartItemId);

    Long fetchCartId(Long userId);

    void boughtProductAllItemsFromCart(Long cartItemId);
}
