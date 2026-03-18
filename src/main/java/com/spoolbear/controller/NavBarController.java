package com.spoolbear.controller;

import com.spoolbear.model.response.CommonResponse;
import com.spoolbear.model.response.NavBarResponse;
import com.spoolbear.service.NavBarService;
import com.spoolbear.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v0/nav-bar")
public class NavBarController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavBarController.class);

    private final NavBarService navBarService;

    @Autowired
    public NavBarController(NavBarService navBarService) {
        this.navBarService = navBarService;
    }

    @GetMapping(path = "/active")
    public ResponseEntity<CommonResponse<List<NavBarResponse>>> getActiveNavBarData(){
        LOGGER.info("{} Start execute get active nav bar data {}", Constant.DOTS, Constant.DOTS);
        CommonResponse<List<NavBarResponse>> response = navBarService.getActiveNavBarData();
        LOGGER.info("{} End execute get active nav bar data {}", Constant.DOTS, Constant.DOTS);
        return new ResponseEntity<>(response ,HttpStatus.OK);
    }

}