package org.apache.coyote.http.util.exception;

public class UnsupportedHttpMethodException extends IllegalArgumentException {

    public UnsupportedHttpMethodException() {
        super("지원하지 않는 HTTP Method 입니다.");
    }
}
