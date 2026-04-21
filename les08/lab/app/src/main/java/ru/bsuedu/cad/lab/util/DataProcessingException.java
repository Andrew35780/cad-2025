package ru.bsuedu.cad.lab.util;


public class DataProcessingException extends RuntimeException {
     public DataProcessingException(String message) {
        super(message);
    }

    public DataProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
