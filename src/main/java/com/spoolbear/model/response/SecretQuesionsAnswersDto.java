package com.spoolbear.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecretQuesionsAnswersDto {
    private Long secretQuestionId;
    private String secretQuestion;
    private String answer;
}
