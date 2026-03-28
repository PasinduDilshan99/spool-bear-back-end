package com.spoolbear.controller;

import com.spoolbear.model.request.BrowsingHistoryRequest;
import com.spoolbear.model.request.InsertBrowserHistoryRequest;
import com.spoolbear.model.response.BrowserHistoryResponse;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;
import com.spoolbear.service.BrowserHistoryService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v0/browser-history")
public class BrowserHistoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserHistoryController.class);

    private final BrowserHistoryService browserHistoryService;

    @Autowired
    public BrowserHistoryController(BrowserHistoryService browserHistoryService) {
        this.browserHistoryService = browserHistoryService;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<CommonResponse<InsertResponse>> addHistoryData(@RequestBody InsertBrowserHistoryRequest insertBrowserHistoryRequest) {
        LOGGER.info("{} Start execute create history data {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<InsertResponse> response = browserHistoryService.addHistoryData(insertBrowserHistoryRequest);
        LOGGER.info("{} End execute create history data {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/history-data")
    public ResponseEntity<CommonResponse<BrowserHistoryResponse>> getHistoryData(@RequestBody BrowsingHistoryRequest browsingHistoryRequest){
        LOGGER.info("{} Start execute get history data {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<BrowserHistoryResponse> response = browserHistoryService.getHistoryData(browsingHistoryRequest);
        LOGGER.info("{} End execute get history data {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
