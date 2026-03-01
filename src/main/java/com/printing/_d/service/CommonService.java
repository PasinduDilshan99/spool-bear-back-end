package com.printing._d.service;

public interface CommonService {
    Long getUserIdBySecurityContext();
    Long getUserIdBySecurityContextWithOutException();
    String generateRandomOtp();
}
