package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200,"OK "),
    FOUND(302,"FOUND "),
    UNAUTHORIZED(401,"UNAUTHORIZED "),
    INTERNAL_SERVER_ERROR(500,"INTERNAL SERVER ERROR ");

    private final int code;
    private final String message;

    HttpStatus(final int code, final String message) {
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
