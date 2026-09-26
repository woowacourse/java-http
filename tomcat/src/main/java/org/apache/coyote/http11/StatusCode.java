package org.apache.coyote.http11;

public enum StatusCode {
    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int code;
    private final String description;

    StatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getStatusCode() {
        return code + " " + description;
    }
}
