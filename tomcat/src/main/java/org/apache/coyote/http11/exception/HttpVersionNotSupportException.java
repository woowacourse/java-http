package org.apache.coyote.http11.exception;

public class HttpVersionNotSupportException extends RuntimeException {

    public HttpVersionNotSupportException(final String value) {
        super(String.format("지원하지 않는 http 버전입니다. (%s)", value));
    }
}
