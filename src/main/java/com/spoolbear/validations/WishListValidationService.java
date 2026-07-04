package com.spoolbear.validations;

import com.spoolbear.model.request.WishListInsertRequest;

public interface WishListValidationService {
    void validateWishListInsertRequest(WishListInsertRequest wishListInsertRequest);
}
