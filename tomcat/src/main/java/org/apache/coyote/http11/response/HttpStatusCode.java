package org.apache.coyote.http11.response;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int code;
    private final String description;

    HttpStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getStatus() {
        return code + " " + description;
    }
}
