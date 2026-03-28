package com.spoolbear.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrowsingHistoryRequest {
    private Date from;
    private Date to;
    private Integer noOfLastDays;
    private int pageSize;
    private int pageNumber;
}
