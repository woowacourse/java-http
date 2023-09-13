package org.apache.coyote.http11.response;

public enum HttpStatusCode {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    METHOD_NOT_ALLOWED(405, "METHOD NOT ALLOWED"),
    NOT_FOUND(404, "NOT_FOUND");

    private final int code;
    private final String type;

    HttpStatusCode(final int code, final String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
