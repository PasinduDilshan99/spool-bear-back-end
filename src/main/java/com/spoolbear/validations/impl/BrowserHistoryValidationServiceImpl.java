package com.spoolbear.validations.impl;

import com.spoolbear.model.request.InsertBrowserHistoryRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryListRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryRequest;
import com.spoolbear.validations.BrowserHistoryValidationService;
import org.springframework.stereotype.Service;

@Service
public class BrowserHistoryValidationServiceImpl implements BrowserHistoryValidationService {
    @Override
    public void validateInsertBrowserHistoryRequest(InsertBrowserHistoryRequest insertBrowserHistoryRequest) {

    }

    @Override
    public void validateRemoveBrowserHistoryRequest(RemoveBrowserHistoryRequest removeBrowserHistoryRequest) {

    }

    @Override
    public void validateRemoveBrowserHistoryListRequest(RemoveBrowserHistoryListRequest removeBrowserHistoryListRequest) {

    }
}
