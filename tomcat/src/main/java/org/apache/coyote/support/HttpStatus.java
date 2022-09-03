package org.apache.coyote.support;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
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

    public String toStatusLine() {
        return String.format("%s %d %s ", HttpVersion.HTTP11.getValue(), code, reason);
    }
}
