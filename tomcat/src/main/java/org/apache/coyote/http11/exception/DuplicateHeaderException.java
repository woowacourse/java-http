package org.apache.coyote.http11.exception;

public class DuplicateHeaderException extends RuntimeException {

    public DuplicateHeaderException() {
        super("동일한 헤더를 2개 이상 등록할 수 없습니다.");
    }
}
