package com.spoolbear.security.service;

import com.spoolbear.model.request.PasswordChangeRequest;
import com.spoolbear.model.request.ResetPasswordRequest;
import com.spoolbear.model.request.SecretQuestionsUpdateRequest;
import com.spoolbear.model.request.UsernamePasswordValidationRequest;
import com.spoolbear.model.response.*;
import com.spoolbear.security.model.LoginRequest;
import com.spoolbear.security.model.LoginResponse;
import com.spoolbear.security.model.RegisterUser;
import com.spoolbear.security.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface AuthService {
    CommonResponse<String> signup(RegisterUser registerUser);

    CommonResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletResponse response);

    CommonResponse<String> logout(HttpServletRequest request, HttpServletResponse response);

    CommonResponse<User> getCurrentUserProfile();

    CommonResponse<ResetPasswordResponse> resetPassword(ResetPasswordRequest resetPasswordRequest);

    CommonResponse<PasswordChangeResponse> changePassword(PasswordChangeRequest passwordChangeRequest);

    CommonResponse<UpdateResponse> updateSecretQuestions(SecretQuestionsUpdateRequest secretQuestionsUpdateRequest);

    CommonResponse<List<SecretQuestionResponse>> getActiveScretQuestions();

    CommonResponse<List<SecretQuesionsAnswersDto>> getActiveScretQuestionsByUser();

    CommonResponse<Boolean> usernamePasswordValidation(UsernamePasswordValidationRequest usernamePasswordValidationRequest);
}
