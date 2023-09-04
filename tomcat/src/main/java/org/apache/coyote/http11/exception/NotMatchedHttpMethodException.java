package org.apache.coyote.http11.exception;

public class NotMatchedHttpMethodException extends RuntimeException {

    public NotMatchedHttpMethodException() {
        super("Not matched http method");
    }
}

