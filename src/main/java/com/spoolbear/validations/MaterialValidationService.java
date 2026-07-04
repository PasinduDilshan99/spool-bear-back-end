package com.spoolbear.validations;

import com.spoolbear.model.request.MaterialDetailsRequest;

public interface MaterialValidationService {
    void validateMaterialDetailsRequest(MaterialDetailsRequest materialDetailsRequest);
}
