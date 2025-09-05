package org.apache.coyote.response.responseLine;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),

    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed")
    ;

    private final int statusCode;
    private final String statusMessage;

    HttpStatus(final int statusCode, final String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String toCombine() {
        return statusCode + " " + statusMessage;
    }
}
