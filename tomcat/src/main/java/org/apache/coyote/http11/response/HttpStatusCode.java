package org.apache.coyote.http11.response;

public enum HttpStatusCode {
    OK("OK", 200),
    FOUND("Found", 302);

    private final String message;
    private final int code;

    HttpStatusCode(String message, int code) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String toString() {
        return code + " " + message;
    }
}
