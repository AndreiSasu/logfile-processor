package com.example.demo.parser;

public class ParseException extends Throwable {

    public ParseException(Throwable throwable) {
        super(throwable);
    }

    public ParseException(String s) {
        super(s);
    }

    public ParseException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
