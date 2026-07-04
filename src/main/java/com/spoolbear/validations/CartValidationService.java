package com.spoolbear.validations;

import com.spoolbear.model.request.FetchCartRequest;
import com.spoolbear.model.request.InsertItemToCartRequest;
import com.spoolbear.model.request.RemoveAllFromCartRequest;
import com.spoolbear.model.request.RemoveItemFromCartRequest;

public interface CartValidationService {
    void validateInsertItemToCartRequest(InsertItemToCartRequest insertItemToCartRequest);

    void validateFetchCartRequest(FetchCartRequest fetchCartRequest);

    void validateRemoveItemFromCartRequest(RemoveItemFromCartRequest removeItemFromCartRequest);

    void validateRemoveAllFromCartRequest(RemoveAllFromCartRequest removeAllFromCartRequest);
}
