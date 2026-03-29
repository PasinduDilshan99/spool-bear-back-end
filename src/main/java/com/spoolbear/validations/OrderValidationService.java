package com.spoolbear.validations;

import com.spoolbear.model.request.DesignOrderInsertRequest;
import com.spoolbear.model.request.PrintingOrderInsertRequest;

public interface OrderValidationService {
    void validatePrintingOrderRequest(PrintingOrderInsertRequest printingOrderInsertRequest);

    void validateDesignOrderInsertRequest(DesignOrderInsertRequest designOrderInsertRequest);
}
