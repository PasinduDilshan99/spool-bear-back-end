package com.printing._d.exception;

public class UnAuthenticateErrorExceptionHandler extends RuntimeException{
    public UnAuthenticateErrorExceptionHandler(String message) {
        super(message);
    }
}
