package com.spoolbear.service;

public interface CommonService {
    Long getUserIdBySecurityContext();
    Long getUserIdBySecurityContextWithOutException();
    String generateRandomOtp();
}
