package com.techcourse.exception;

public class UnsupportedHttpMethodException extends RuntimeException {
    public UnsupportedHttpMethodException(String e) {
        super(e + "is not a supporting http method.");
    }
}
