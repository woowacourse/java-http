package org.apache.tomcat.util.http;

public enum HttpStatus {
    OK(200),
    FOUND(302);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
