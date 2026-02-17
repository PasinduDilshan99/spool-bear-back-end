package com.printing._d.repository;

import com.printing._d.model.response.NavBarResponse;

import java.util.List;

public interface NavBarRepository {
    List<NavBarResponse> getActiveNavBarData();
}
