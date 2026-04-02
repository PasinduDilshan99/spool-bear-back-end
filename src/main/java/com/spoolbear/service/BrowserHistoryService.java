package com.spoolbear.service;

import com.spoolbear.model.request.BrowsingHistoryRequest;
import com.spoolbear.model.request.InsertBrowserHistoryRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryListRequest;
import com.spoolbear.model.request.RemoveBrowserHistoryRequest;
import com.spoolbear.model.response.BrowserHistoryResponse;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.model.response.UpdateResponse;

public interface BrowserHistoryService {
    CommonResponse<InsertResponse> addHistoryData(InsertBrowserHistoryRequest insertBrowserHistoryRequest);

    CommonResponse<BrowserHistoryResponse> getHistoryData(BrowsingHistoryRequest browsingHistoryRequest);

    CommonResponse<UpdateResponse> removeHistoryData(RemoveBrowserHistoryRequest removeBrowserHistoryRequest);

    CommonResponse<UpdateResponse> removeAllHistoryData();

    CommonResponse<UpdateResponse> removeHistoryDataList(RemoveBrowserHistoryListRequest removeBrowserHistoryListRequest);
}
