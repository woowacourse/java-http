package org.apache.coyote.http11;

public class InvalidRequestLineException extends Http11Exception {

    public InvalidRequestLineException() {
        super("올바르지 않은 RequestLine 형식입니다.");
    }
}
