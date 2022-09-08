package org.apache.catalina.exception;

public class ReflectionsCreateException extends InternalServerException {
    public ReflectionsCreateException() {
        super("리플렉션으로 객체 생성에서 문제가 생겼습니다.");
    }
}
