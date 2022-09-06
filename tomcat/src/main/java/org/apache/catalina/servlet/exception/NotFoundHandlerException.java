package org.apache.catalina.servlet.exception;

public class NotFoundHandlerException extends RuntimeException {
    public NotFoundHandlerException() {
        super("지원하는 핸들러가 없습니다.");
    }
}
