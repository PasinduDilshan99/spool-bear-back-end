package com.printing._d.service;

import com.printing._d.model.response.CommonResponse;
import com.printing._d.model.response.NavBarResponse;

import java.util.List;

public interface NavBarService {
    CommonResponse<List<NavBarResponse>> getActiveNavBarData();
}
