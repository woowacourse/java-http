package org.apache.http.value;

public enum StatusCode {

    OK(200, "OK"),
    FOUND(302, "FOUND");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
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
