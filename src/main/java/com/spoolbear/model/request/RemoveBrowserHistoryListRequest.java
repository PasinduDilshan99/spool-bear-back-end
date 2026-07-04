package com.spoolbear.model.request;

import lombok.Data;

import java.util.List;

@Data
public class RemoveBrowserHistoryListRequest {
    private List<Long> historyDataIds;
}
