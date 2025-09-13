package org.apache.coyote.http11.exception;

public class Http11ParseException extends Exception {

    private final ParseError error;

    public Http11ParseException(ParseError error) {
        super(error.getMessage());
        this.error = error;
    }

    public int getStatusCode() {
        return error.getStatusCode();
    }

    public String getErrorMessage() {
        return error.getMessage();
    }
}
