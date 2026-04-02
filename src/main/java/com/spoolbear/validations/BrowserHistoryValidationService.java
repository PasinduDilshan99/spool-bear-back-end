package com.spoolbear.validations;

import com.spoolbear.model.request.InsertBrowserHistoryRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryListRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryRequest;

public interface BrowserHistoryValidationService {
    void validateInsertBrowserHistoryRequest(InsertBrowserHistoryRequest insertBrowserHistoryRequest);

    void validateRemoveBrowserHistoryRequest(RemoveBrowserHistoryRequest removeBrowserHistoryRequest);

    void validateRemoveBrowserHistoryListRequest(RemoveBrowserHistoryListRequest removeBrowserHistoryListRequest);
}
