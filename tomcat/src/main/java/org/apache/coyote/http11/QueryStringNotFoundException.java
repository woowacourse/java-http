package org.apache.coyote.http11;

public class QueryStringNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Query String Not Found";

    public QueryStringNotFoundException() {
        super(MESSAGE);
    }
}
