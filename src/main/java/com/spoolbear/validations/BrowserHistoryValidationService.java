package com.spoolbear.validations;

import com.spoolbear.model.request.InsertBrowserHistoryRequest;

public interface BrowserHistoryValidationService {
    void validateInsertBrowserHistoryRequest(InsertBrowserHistoryRequest insertBrowserHistoryRequest);
}
