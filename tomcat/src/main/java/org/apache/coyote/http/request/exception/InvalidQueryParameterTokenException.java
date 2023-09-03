package org.apache.coyote.http.request.exception;

public class InvalidQueryParameterTokenException extends IllegalArgumentException {

    public InvalidQueryParameterTokenException() {
        super("유효한 쿼리 파라미터가 아닙니다.");
    }
}
