package org.apache.coyote.http11;

public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(400, "Not Found"),
    BAD_REQUEST(404, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error")
    ;

    private final int code;
    private final String text;

    HttpStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
