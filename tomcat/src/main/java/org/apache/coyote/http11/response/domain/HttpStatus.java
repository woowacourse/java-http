package org.apache.coyote.http11.response.domain;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),

    NOT_MODIFIED(304, "Not Modified"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    HTTP_VERSION_NOT_FOUND(500, "HTTP Version Not Supported");


    private final int statusCode;
    private final String statusMessage;

    HttpStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getCombinedHttpStatus() {
        return statusCode + " " + statusMessage;
    }
}
