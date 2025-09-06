package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getStatusLine() {
        return String.format("HTTP/1.1 %d %s", code, message);
    }
}
