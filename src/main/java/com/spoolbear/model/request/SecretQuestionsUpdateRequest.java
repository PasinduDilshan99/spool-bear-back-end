package com.spoolbear.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecretQuestionsUpdateRequest {
    private List<SecretQuestion> addQuestions;
    private List<Long> removeQuestionsIds;
    private List<SecretQuestion> updateQuestions;

    @Data
    public static class SecretQuestion{
        private Long question;
        private String answer;
    }
}
