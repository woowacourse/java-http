package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "OK"),
    REDIRECT(302, "Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_FOUND(404, "Not Found"),
    ;

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
