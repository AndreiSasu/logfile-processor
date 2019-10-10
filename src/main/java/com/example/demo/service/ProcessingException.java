package com.example.demo.service;

public class ProcessingException extends Exception {

    public ProcessingException(String s) {
        super(s);
    }

    public ProcessingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ProcessingException(Throwable throwable) {
        super(throwable);
    }
}
