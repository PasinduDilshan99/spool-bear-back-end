package com.spoolbear.validations;

import com.spoolbear.model.request.PasswordChangeRequest;
import com.spoolbear.model.request.ResetPasswordRequest;
import com.spoolbear.model.request.SecretQuestionsUpdateRequest;

public interface AuthValidationService {
    void validateResetPasswordRequest(ResetPasswordRequest resetPasswordRequest);

    void validatePasswordChangeRequest(PasswordChangeRequest passwordChangeRequest);

    void validateSecretQuestionsUpdateRequest(SecretQuestionsUpdateRequest secretQuestionsUpdateRequest);
}
