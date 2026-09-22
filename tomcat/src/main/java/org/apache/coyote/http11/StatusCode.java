package org.apache.coyote.http11;

public enum StatusCode {
    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private int code;
    private String description;
    private StatusCode(int code, String description) {
        this.code = code;
    }

    public String getStatusCode() {
        return code + " " + description;
    }
}
