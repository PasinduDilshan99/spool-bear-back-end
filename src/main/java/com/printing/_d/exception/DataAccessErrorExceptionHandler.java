package com.printing._d.exception;

public class DataAccessErrorExceptionHandler extends RuntimeException{
    public DataAccessErrorExceptionHandler(String message) {
        super(message);
    }
}
