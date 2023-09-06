package org.apache.catalina.servlet.request;

public class InvalidStartLineException extends RuntimeException {

    public InvalidStartLineException() {
        super("Request 의 Request Line 이 올바르지 않습니다.");
    }
}
