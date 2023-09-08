package org.apache.coyote.httprequest.exception;

public class InvalidHttpVersionException extends IllegalArgumentException {

    public InvalidHttpVersionException() {
        super("잘못된 Http 프로토콜 버전 형식입니다.");
    }
}
