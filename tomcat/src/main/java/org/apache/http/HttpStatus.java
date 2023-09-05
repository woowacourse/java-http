package org.apache.http;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "CREATED"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND");

    private static final HttpStatus[] VALUES;

    static {
        VALUES = values();
    }

    private final int statusCode;
    private final String statusString;

    HttpStatus(int statusCode, final String statusString) {
        this.statusCode = statusCode;
        this.statusString = statusString;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusString() {
        return statusString;
    }

    public static HttpStatus of(final int statusCode) {
        final HttpStatus status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException();
        }
        return status;
    }

    private static HttpStatus resolve(int statusCode) {
        for (final HttpStatus status : VALUES) {
            if (status.statusCode == statusCode) {
                return status;
            }
        }
        return null;
    }
}
