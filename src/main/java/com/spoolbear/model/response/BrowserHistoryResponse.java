package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BrowserHistoryResponse {
    private Integer totalCount;
    private List<HistoryResponse> history;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class HistoryResponse{
        private Long id;
        private String name;
        private Long productId;
        private Long userId;
        private LocalDateTime createdAt;
        private String statusName;
    }
}
