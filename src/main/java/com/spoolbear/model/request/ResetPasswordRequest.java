package com.spoolbear.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {
    private String username;
    private Long secretQuestion1;
    private String secretQuestion1Answer;
    private Long secretQuestion2;
    private String secretQuestion2Answer;
    private Long secretQuestion3;
    private String secretQuestion3Answer;
    private String newPassword;
    private String confirmPassword;
}
