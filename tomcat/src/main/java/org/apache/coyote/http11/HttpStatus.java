package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found")

    ;

    private final int code;
    private final String name;

    HttpStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getDescription() {
        return code + " " + name;
    }
}
