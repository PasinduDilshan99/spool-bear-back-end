package com.spoolbear.exception;

public class DataRetrieveFailedErrorExceptionHandler extends RuntimeException{
    public DataRetrieveFailedErrorExceptionHandler(String message) {
        super(message);
    }
}
