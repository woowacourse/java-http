package org.apache.coyote.http11.common;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_GATEWAY(502, "BAD_GATEWAY"),
    ;

    private static final String STATUS_FORMAT = "%s %s ";

    private final int code;
    private final String text;

    HttpStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public String message() {
        return String.format(STATUS_FORMAT, code, text);
    }
}
