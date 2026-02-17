package com.printing._d.exception;

public class DataNotFoundErrorExceptionHandler extends RuntimeException{
    public DataNotFoundErrorExceptionHandler(String message) {
        super(message);
    }
}
