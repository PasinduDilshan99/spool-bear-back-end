package com.spoolbear.exception;

public class InternalServerErrorExceptionHandler extends RuntimeException{
    public InternalServerErrorExceptionHandler(String message) {
        super(message);
    }
}
