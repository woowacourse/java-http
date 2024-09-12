package org.apache.http.response;

public enum HttpStatus {
    OK(200, "OK"),

    FOUND(302, "Found"),

    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int code;
    private final String reason;

    HttpStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return code + " " + reason;
    }
}
