package org.apache.coyote.http.util.exception;

public class InvalidHeaderSizeException extends IllegalArgumentException {

    public InvalidHeaderSizeException() {
        super("유효한 헤더 크기가 아닙니다.");
    }
}
