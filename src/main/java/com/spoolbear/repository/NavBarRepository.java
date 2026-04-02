package com.spoolbear.repository;

import com.spoolbear.model.response.NavBarResponse;

import java.util.List;

public interface NavBarRepository {
    List<NavBarResponse> getActiveNavBarData();
}
