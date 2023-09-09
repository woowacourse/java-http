package org.apache.coyote.http11.exception;

public class MissingRequestBody extends RuntimeException {

    public MissingRequestBody() {
        super("누락된 request body가 있습니다.");
    }
}
