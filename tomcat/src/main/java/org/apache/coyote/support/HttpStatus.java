package org.apache.coyote.support;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "Created"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    HttpStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String text() {
        return String.format("%s %s", code, message);
    }
}
