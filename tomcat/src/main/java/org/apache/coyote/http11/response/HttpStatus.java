package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200, "OK"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;
    private final String reasonPhrase;

    HttpStatus(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public static HttpStatus fromCode(int code) {
        for (HttpStatus status : values()) {
            if (status.getStatusCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
