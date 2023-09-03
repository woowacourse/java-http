package org.apache.catalina.servlet.session;

public class InvalidCookieException extends RuntimeException {

    public InvalidCookieException() {
        super("Cookie 가 올바르지 않습니다.");
    }
}
