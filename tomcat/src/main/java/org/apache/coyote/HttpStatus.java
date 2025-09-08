package org.apache.coyote;

public enum HttpStatus {

    // 2xx
    OK(200, "200 OK"),

    // 4xx
    METHOD_NOT_ALLOWED(405, "405 METHOD_NOT_ALLOWED");

    private final int code;
    private final String prase;

    HttpStatus(int code, String prase) {
        this.code = code;
        this.prase = prase;
    }

    public int getCode() {
        return code;
    }

    public String getPrase() {
        return prase;
    }
}
