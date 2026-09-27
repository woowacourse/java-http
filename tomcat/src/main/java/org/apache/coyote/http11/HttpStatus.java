package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
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
