package org.apache.coyote;

public enum HttpStatus {

    // 2XX
    OK(200, "OK"),
    // 3XX
    FOUND(302, "Found"),
    // 4XX
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    // 5XX
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int value;
    private final String reason;

    HttpStatus(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public int getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}
