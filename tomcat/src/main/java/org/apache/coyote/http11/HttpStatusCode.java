package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpStatusCode of(int code) {
        for (HttpStatusCode httpStatusCode : values()) {
            if (httpStatusCode.code == code) {
                return httpStatusCode;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + code + "]");
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }
}
