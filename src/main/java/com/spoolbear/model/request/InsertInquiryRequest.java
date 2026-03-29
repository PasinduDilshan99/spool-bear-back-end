package com.spoolbear.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InsertInquiryRequest {
    private String name;
    private String email;
    private String subject;
    private String message;
}
