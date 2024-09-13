package org.apache.tomcat.http.exception;

public class MethodNotSupportException extends RuntimeException {

    public MethodNotSupportException() {
        super("지원하지 않는 메서드");
    }
}
