package org.apache.coyote.http11.response;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND")
    ;

    private final int code;
    private final String type;

    HttpStatusCode(final int code, final String type) {
        this.code = code;
        this.type = type;
    }

    public String toResponseFormat() {
        return String.format("%d %s", code, type);
    }
}
