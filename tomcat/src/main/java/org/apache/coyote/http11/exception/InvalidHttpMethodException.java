package org.apache.coyote.http11.exception;

public class InvalidHttpMethodException extends Http11Exception {

    public InvalidHttpMethodException() {
        super("올바르지 않은 HttpMethod 형식입니다.");
    }
}
