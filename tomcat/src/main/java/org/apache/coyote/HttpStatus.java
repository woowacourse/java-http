package org.apache.coyote;

public enum HttpStatus {

    // 2xx
    OK(200, "200 OK"),

    // 4xx
    METHOD_NOT_ALLOWED(405, "405 METHOD_NOT_ALLOWED");

    private final int code;
    private final String phrase;

    HttpStatus(int code, String phrase) {
        this.code = code;
        this.phrase = phrase;
    }

    public int getCode() {
        return code;
    }

    public String getPhrase() {
        return phrase;
    }
}
