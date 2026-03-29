package com.spoolbear.repository;

import com.spoolbear.model.request.InsertInquiryRequest;

public interface ContactUsRepository {
    boolean addInquiry(InsertInquiryRequest insertInquiryRequest);
}
