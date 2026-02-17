package com.printing._d.security.service;

import com.printing._d.model.response.CommonResponse;
import com.printing._d.security.model.LoginRequest;
import com.printing._d.security.model.LoginResponse;
import com.printing._d.security.model.RegisterUser;
import com.printing._d.security.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    CommonResponse<String> signup(RegisterUser registerUser);

    CommonResponse<LoginResponse> login(LoginRequest loginRequest, HttpServletResponse response);

    CommonResponse<String> logout(HttpServletRequest request, HttpServletResponse response);

    CommonResponse<User> getCurrentUserProfile();
}
