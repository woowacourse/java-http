package org.apache.coyote.controller.util;

public enum ResponseManager {

    KEY_VALUE_DELIMITER(": "),
    RESPONSE_HEADER_200("HTTP/1.1 200 OK "),
    HTTP_302_FOUND("HTTP/1.1 302 Found "),
    SET_COOKIE("Set-Cookie"),
    ;

    public String getHeader() {
        return this.header;
    }

    private final String header;

    ResponseManager(final String header) {
        this.header = header;
    }
}
