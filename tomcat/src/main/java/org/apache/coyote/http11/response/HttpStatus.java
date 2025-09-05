package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK(200, "OK"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int code;
    private final String message;

    HttpStatus(
            final int code,
            final String message
    ) {
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
