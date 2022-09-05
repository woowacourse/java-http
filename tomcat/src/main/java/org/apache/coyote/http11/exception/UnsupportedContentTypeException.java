package org.apache.coyote.http11.exception;

public class UnsupportedContentTypeException extends RuntimeException {

    public UnsupportedContentTypeException() {
        super("지원하지 않는 Content Type입니다.");
    }
}
