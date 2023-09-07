package org.apache.coyote.http11.exception;

public class UnsupportedContentTypeException extends RuntimeException {

    public UnsupportedContentTypeException() {
        super("지원되지 않는 Content Type 입니다.");
    }
}
