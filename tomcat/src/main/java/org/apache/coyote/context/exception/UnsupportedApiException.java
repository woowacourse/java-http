package org.apache.coyote.context.exception;

public class UnsupportedApiException extends IllegalArgumentException {

    public UnsupportedApiException() {
        super("처리할 수 없는 요청입니다.");
    }
}
