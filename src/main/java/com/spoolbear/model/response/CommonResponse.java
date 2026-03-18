package com.spoolbear.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
    private int code;
    private String status;
    private String message;
    private T data;
    private Instant timestamp = Instant.now();
}
