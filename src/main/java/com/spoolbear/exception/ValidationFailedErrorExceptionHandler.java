package com.spoolbear.exception;

import com.spoolbear.model.response.ValidationFailedResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationFailedErrorExceptionHandler extends RuntimeException{

    private List<ValidationFailedResponse> validationFailedResponses;

    public ValidationFailedErrorExceptionHandler(String message, List<ValidationFailedResponse> validationFailedResponses) {
        super(message);
        this.validationFailedResponses = validationFailedResponses;
    }

}
