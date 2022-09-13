package org.apache.coyote.exception;

public class InvalidQueryParamKeyException extends IllegalArgumentException {

    public InvalidQueryParamKeyException() {
        super("잘못된 쿼리 파라미터 KEY 값입니다.");
    }
}
