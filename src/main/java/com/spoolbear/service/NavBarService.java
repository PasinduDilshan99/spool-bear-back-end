package com.spoolbear.service;

import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.NavBarResponse;

import java.util.List;

public interface NavBarService {
    CommonResponse<List<NavBarResponse>> getActiveNavBarData();
}
