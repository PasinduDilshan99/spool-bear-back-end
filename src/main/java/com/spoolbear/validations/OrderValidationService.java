package com.spoolbear.validations;

import com.spoolbear.model.request.DesignOrderInsertRequest;
import com.spoolbear.model.request.PrintingOrderInsertRequest;
import com.spoolbear.model.request.ProductOrderInsertRequest;

public interface OrderValidationService {
    void validatePrintingOrderRequest(PrintingOrderInsertRequest printingOrderInsertRequest);

    void validateDesignOrderInsertRequest(DesignOrderInsertRequest designOrderInsertRequest);

    void validateProductOrderInsertRequest(ProductOrderInsertRequest productOrderInsertRequest);
}
