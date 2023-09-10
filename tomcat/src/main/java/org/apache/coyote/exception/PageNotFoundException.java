package org.apache.coyote.exception;

public class PageNotFoundException extends RuntimeException {
    private final String message;

    public PageNotFoundException(final String path) {
        this.message = "요청하신 페이지를 찾을 수 없습니다 :" + path;
    }
}
