package org.apache.coyote.http.util.exception;

public class UnsupportedHttpVersionException extends IllegalArgumentException {

    public UnsupportedHttpVersionException() {
        super("지원하지 않는 HTTP 버전입니다.");
    }
}
