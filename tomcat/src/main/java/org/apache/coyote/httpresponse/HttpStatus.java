package org.apache.coyote.httpresponse;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private static final String DELIMITER = " ";
    private final int value;
    private final String reasonPhrase;

    HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public String getHttpStatus() {
        return value + DELIMITER + reasonPhrase;
    }
}
