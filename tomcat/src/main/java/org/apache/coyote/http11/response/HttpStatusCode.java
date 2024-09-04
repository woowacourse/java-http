package org.apache.coyote.http11.response;

public enum HttpStatusCode {
    OK("OK", 200),
    FOUND("Found", 302);

    private final String name;
    private final int code;

    HttpStatusCode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return code + " " + name;
    }
}
