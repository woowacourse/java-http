package org.apache.coyote.http11;

public enum HttpStatus {
    OK("OK", 200),

    FOUND("Found", 302),

    UNAUTHORIZED("Unauthorized", 401),
    NOT_FOUND("Not Found", 404);

    private String name;
    private final int code;

    HttpStatus(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public boolean is2xx() {
        return code / 100 == 2;
    }

    public boolean is3xx() {
        return code / 100 == 3;
    }
}
