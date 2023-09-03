package org.apache.coyote.httprequest.exception;

public class InvalidPragmaHeaderException extends IllegalArgumentException {

    public InvalidPragmaHeaderException() {
        super("잘못된 Pragma 헤더 입니다.");
    }
}
