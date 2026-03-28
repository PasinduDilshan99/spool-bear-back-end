package com.spoolbear.service;

import com.spoolbear.model.request.BrowsingHistoryRequest;
import com.spoolbear.model.request.InsertBrowserHistoryRequest;
import com.spoolbear.model.response.BrowserHistoryResponse;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;

public interface BrowserHistoryService {
    CommonResponse<InsertResponse> addHistoryData(InsertBrowserHistoryRequest insertBrowserHistoryRequest);

    CommonResponse<BrowserHistoryResponse> getHistoryData(BrowsingHistoryRequest browsingHistoryRequest);
}
