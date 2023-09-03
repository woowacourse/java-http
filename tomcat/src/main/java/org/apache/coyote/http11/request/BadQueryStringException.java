package org.apache.coyote.http11.request;

public class BadQueryStringException extends RuntimeException {

    public BadQueryStringException() {
        super("QueryString이 잘못되었습니다.");
    }
}
