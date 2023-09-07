package org.apache.coyote.http11.response;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found"),
    METHOD_NOT_ALLOWED("405 Method Not Allowed");

    private final String code;

    HttpStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
