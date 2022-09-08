package org.apache.catalina.exception;

public class InvalidHttpMethodException extends InternalServerException {
    public InvalidHttpMethodException() {
        super("잘못된 HTTP METHOD입니다.");
    }
}
