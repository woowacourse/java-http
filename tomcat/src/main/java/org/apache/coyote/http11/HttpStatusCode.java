package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK_200(200, "OK"),
    FOUND_302(302, "Found"),
    ;

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
