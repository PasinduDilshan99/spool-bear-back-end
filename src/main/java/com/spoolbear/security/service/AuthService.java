package com.spoolbear.security.service;

import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.security.model.LoginRequest;
import com.spoolbear.security.model.LoginResponse;
import com.spoolbear.security.model.RegisterUser;
import com.spoolbear.security.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    CommonResponse<String> signup(RegisterUser registerUser);

    CommonResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletResponse response);

    CommonResponse<String> logout(HttpServletRequest request, HttpServletResponse response);

    CommonResponse<User> getCurrentUserProfile();
}
