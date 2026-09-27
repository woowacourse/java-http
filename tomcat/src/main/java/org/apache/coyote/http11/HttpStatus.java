package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict");

    int value;
    String message;

    HttpStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getHttpStatus() {
        return this.value + " " + this.message;
    }
}
