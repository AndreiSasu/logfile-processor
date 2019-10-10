package com.example.demo.service;

public interface Processor<I, O> {
    O process(I input) throws ProcessingException;
}
