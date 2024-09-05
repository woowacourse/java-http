package org.apache.coyote.response;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int statusCode;
    private final String statusMessage;

    HttpStatusCode(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String buildMessage() {
        return "HTTP/1.1 " + statusCode + " " + statusMessage + " ";
    }
}
