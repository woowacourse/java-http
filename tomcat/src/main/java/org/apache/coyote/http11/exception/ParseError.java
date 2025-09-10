package org.apache.coyote.http11.exception;

public enum ParseError {

    INVALID_REQUEST_LINE(400, "Invalid request line"),
    INVALID_HTTP_METHOD(400, "Invalid http method"),
    INVALID_STATUS_CODE(400, "Invalid status code format");

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
