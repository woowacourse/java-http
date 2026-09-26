package org.apache.coyote.request;

public class UnknownMethodException extends IllegalArgumentException {

    public UnknownMethodException(String method, IllegalArgumentException cause) {
        super("지원하지 않는 HTTP 메서드입니다: " + method, cause);
    }
}
