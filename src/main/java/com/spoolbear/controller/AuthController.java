package com.spoolbear.controller;

import com.spoolbear.model.request.PasswordChangeRequest;
import com.spoolbear.model.request.ResetPasswordRequest;
import com.spoolbear.model.request.SecretQuestionsUpdateRequest;
import com.spoolbear.model.request.UsernamePasswordValidationRequest;
import com.spoolbear.model.response.*;
import com.spoolbear.security.model.LoginRequest;
import com.spoolbear.security.model.LoginResponse;
import com.spoolbear.security.model.RegisterUser;
import com.spoolbear.security.model.User;
import com.spoolbear.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<CommonResponse<String>> signup(@RequestBody RegisterUser registerUser) {
        CommonResponse<String> response = authService.signup(registerUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest,
                                                               HttpServletResponse response){
        CommonResponse<LoginResponse> loginResponse = authService.login(loginRequest, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<CommonResponse<String>> logout(HttpServletRequest request, HttpServletResponse response){
        CommonResponse<String> logoutResponse = authService.logout(request, response);
        return ResponseEntity.ok(logoutResponse);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<CommonResponse<User>> me(){
        CommonResponse<User> user = authService.getCurrentUserProfile();
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<CommonResponse<ResetPasswordResponse>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        CommonResponse<ResetPasswordResponse> response = authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/change-password")
    public ResponseEntity<CommonResponse<PasswordChangeResponse>> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest){
        CommonResponse<PasswordChangeResponse> response = authService.changePassword(passwordChangeRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/update-secret-questions")
    public ResponseEntity<CommonResponse<UpdateResponse>> updateSecretQuestions(@RequestBody SecretQuestionsUpdateRequest secretQuestionsUpdateRequest){
        CommonResponse<UpdateResponse> response = authService.updateSecretQuestions(secretQuestionsUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/secret-questions")
    public ResponseEntity<CommonResponse<List<SecretQuestionResponse>>> getActiveScretQuestions(){
        CommonResponse<List<SecretQuestionResponse>> response = authService.getActiveScretQuestions();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/secret-questions-by-user")
    public ResponseEntity<CommonResponse<List<SecretQuesionsAnswersDto>>> getActiveScretQuestionsByUser(){
        CommonResponse<List<SecretQuesionsAnswersDto>> response = authService.getActiveScretQuestionsByUser();
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/username-password-validation")
    public ResponseEntity<CommonResponse<Boolean>> usernamePasswordValidation(@RequestBody UsernamePasswordValidationRequest usernamePasswordValidationRequest){
        CommonResponse<Boolean> response = authService.usernamePasswordValidation(usernamePasswordValidationRequest);
        return ResponseEntity.ok(response);
    }

}
