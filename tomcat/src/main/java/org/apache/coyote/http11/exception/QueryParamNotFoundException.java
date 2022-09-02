package org.apache.coyote.http11.exception;

public class QueryParamNotFoundException extends RuntimeException {

    public QueryParamNotFoundException() {
        super("잘못된 query param 압니다.");
    }
}
