package com.spoolbear.repository;

import com.spoolbear.model.request.BrowsingHistoryRequest;
import com.spoolbear.model.request.InsertBrowserHistoryRequest;
import com.spoolbear.model.response.BrowserHistoryResponse;

public interface BrowserHistoryRepository {
    void addHistoryData(InsertBrowserHistoryRequest insertBrowserHistoryRequest, Long userId);

    BrowserHistoryResponse getHistoryData(Long userId, BrowsingHistoryRequest browsingHistoryRequest);
}
