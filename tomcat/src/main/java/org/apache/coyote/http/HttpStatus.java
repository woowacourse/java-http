package org.apache.coyote.http;

public enum HttpStatus {
    OK("OK", 200),
    CREATED("Created", 201),
    ACCEPTED("Accepted", 202),

    FOUND("Found", 302),

    BAD_REQUEST("Bad Request", 400),
    UNAUTHORIZED("Unauthorized", 401),
    FORBIDDEN("Forbidden", 403),
    NOT_FOUND("Not Found", 404),

    INTERNAL_SERVER_ERROR("Internal Server Error", 500),
    NOT_IMPLEMENTED("Not Implemented", 501),
    BAD_GATEWAY("Bad Gateway", 502),
    SERVICE_UNAVAILABLE("Service Unavailable", 503),
    GATEWAY_TIME_OUT("Gateway Time-out", 504),
    ;

    private final String statusMessage;
    private final int statusCode;

    HttpStatus(String statusMessage, int statusCode) {
        this.statusMessage = statusMessage;
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
