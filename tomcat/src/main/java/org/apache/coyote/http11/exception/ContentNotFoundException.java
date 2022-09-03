package org.apache.coyote.http11.exception;

public class ContentNotFoundException extends RuntimeException {

    public ContentNotFoundException() {
        super("지원하지 않는 Content-Type 입니다.");
    }
}
