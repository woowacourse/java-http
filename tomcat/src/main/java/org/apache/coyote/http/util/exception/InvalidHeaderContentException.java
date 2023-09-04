package org.apache.coyote.http.util.exception;

public class InvalidHeaderContentException extends IllegalArgumentException {

    public InvalidHeaderContentException() {
        super("유효한 헤더 값이 아닙니다.");
    }
}
