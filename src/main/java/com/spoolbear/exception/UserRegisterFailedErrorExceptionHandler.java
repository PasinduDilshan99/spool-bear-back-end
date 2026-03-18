package com.spoolbear.exception;

public class UserRegisterFailedErrorExceptionHandler extends RuntimeException{
    public UserRegisterFailedErrorExceptionHandler(String message) {
        super(message);
    }
}
