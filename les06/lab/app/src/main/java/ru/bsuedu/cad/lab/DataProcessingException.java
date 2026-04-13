package ru.bsuedu.cad.lab;


public class DataProcessingException extends RuntimeException {
     public DataProcessingException(String message) {
        super(message);
    }

    public DataProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
