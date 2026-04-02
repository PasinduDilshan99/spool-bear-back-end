package com.spoolbear.exception;

public class DataAccessErrorExceptionHandler extends RuntimeException{
    public DataAccessErrorExceptionHandler(String message) {
        super(message);
    }
}
