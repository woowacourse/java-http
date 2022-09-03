package org.apache.coyote.http11.exception;

public class QueryParamNotFoundException extends RuntimeException {

    public QueryParamNotFoundException() {
        super("잘못된 queryParam 입니다.");
    }
}
