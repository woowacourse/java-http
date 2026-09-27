package org.apache.coyote.http11;

public enum HttpStatus {
    OK("OK", 200),
    FOUND("Found", 302),
    SEE_OTHER("See Other", 303),
    BAD_REQUEST("Bad Request", 400),
    UNAUTHORIZED("Unauthorized", 401),
    NOT_FOUND("Not Found", 404),
    METHOD_NOT_ALLOWED("Method Not Allowed", 405),
    HTTP_VERSION_NOT_SUPPORTED("HTTP Version Not Supported", 505);

    private final String name;
    private final int code;

    HttpStatus(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return code + " " + name;
    }
}
