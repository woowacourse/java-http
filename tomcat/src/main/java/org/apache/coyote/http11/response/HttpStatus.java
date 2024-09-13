package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200, "OK"),

    FOUND(302, "FOUND"),

    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),

    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR"),
    ;

    private static final String STATUS_DELIMITER = " ";

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isRedirection() {
        return this.code == 302;
    }

    public String getStatus() {
        return this.code + STATUS_DELIMITER + this.message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
