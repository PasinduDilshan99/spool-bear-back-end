package com.spoolbear.exception;

public class TerminateFailedErrorExceptionHandler extends RuntimeException{
    public TerminateFailedErrorExceptionHandler(String message) {
        super(message);
    }
}
