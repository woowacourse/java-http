package org.apache.coyote.domain.response.statusline;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String message;

    HttpStatusCode(int code, String message) {
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
