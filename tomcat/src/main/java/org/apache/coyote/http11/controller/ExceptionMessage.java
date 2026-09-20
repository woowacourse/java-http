package org.apache.coyote.http11.controller;

public enum ExceptionMessage {
    NOT_SUPPORT_HTTP_METHOD("지원하지 않는 HTTP Method 입니다.");
    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
