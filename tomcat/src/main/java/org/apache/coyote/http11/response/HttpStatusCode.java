package org.apache.coyote.http11.response;

public enum HttpStatusCode {
    OK("OK", 200),
    NO_CONTENT("No Content", 204),
    FOUND("Found", 302),
    ;

    private final String description;
    private final int code;

    HttpStatusCode(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}
