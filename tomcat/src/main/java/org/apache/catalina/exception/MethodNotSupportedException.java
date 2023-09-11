package org.apache.catalina.exception;

public class MethodNotSupportedException extends RuntimeException {

    public MethodNotSupportedException() {
        super("지원하지 않는 메서드입니다.");
    }
}
