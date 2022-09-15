package org.apache.coyote.http11.exception;

public class UnsupportedHttpVersionException extends RuntimeException {

    public UnsupportedHttpVersionException(final String value) {
        super(String.format("지원하지 않는 http 버전입니다. (%s)", value));
    }
}
