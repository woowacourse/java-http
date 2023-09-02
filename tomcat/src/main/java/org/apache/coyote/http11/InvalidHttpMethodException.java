package org.apache.coyote.http11;

public class InvalidHttpMethodException extends Http11Exception {

    public InvalidHttpMethodException() {
        super("올바르지 않은 HttpMethod 형식입니다.");
    }
}
