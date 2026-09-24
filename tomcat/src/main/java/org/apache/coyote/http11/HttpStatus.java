package org.apache.coyote.http11;

public enum HttpStatus {
    OK("OK", 200),
    FOUND("Found", 302),
    SEE_OTHER("See Other", 303),
    UNAUTHORIZED("Unauthorized", 401),
    NOT_FOUND("Not Found", 404);

    private final String name;
    private final int code;

    HttpStatus(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public int code() {
        return code;
    }

    @Override
    public String toString() {
        return code + " " + name;
    }
}
