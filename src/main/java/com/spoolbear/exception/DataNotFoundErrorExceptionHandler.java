package com.spoolbear.exception;

public class DataNotFoundErrorExceptionHandler extends RuntimeException{
    public DataNotFoundErrorExceptionHandler(String message) {
        super(message);
    }
}
