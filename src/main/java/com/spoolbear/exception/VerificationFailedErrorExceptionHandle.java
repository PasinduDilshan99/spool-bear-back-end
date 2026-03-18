package com.spoolbear.exception;

public class VerificationFailedErrorExceptionHandle extends RuntimeException{
    public VerificationFailedErrorExceptionHandle(String message) {
        super(message);
    }
}
