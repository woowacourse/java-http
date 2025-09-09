package org.apache.coyote.http11;

public enum ParseError {

    INVALID_REQUEST_LINE(400, "Invalid request line"),
    INVALID_HTTP_METHOD(400, "Invalid http method");

    private final int statusCode;
    private final String message;

    ParseError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
