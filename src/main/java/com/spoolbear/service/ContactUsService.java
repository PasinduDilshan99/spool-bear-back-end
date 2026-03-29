package com.spoolbear.service;

import com.spoolbear.model.request.InsertInquiryRequest;
import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.InsertResponse;

public interface ContactUsService {
    CommonResponse<InsertResponse> addInquiry(InsertInquiryRequest insertInquiryRequest);
}
