package org.apache.coyote.http.response;

import org.apache.coyote.http.util.HttpConsts;

public enum HttpStatusCode {

    OK("200", "OK"),
    FOUND("302", "Found"),
    BAD_REQUEST("400", "Bad Request"),
    METHOD_NOT_ALLOWED("405", "Method Not Allowed"),
    NOT_FOUND("404", "Not Found"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error");

    private final String code;
    private final String message;

    HttpStatusCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String convertHttpStatusMessage() {
        return code + HttpConsts.SPACE + message;
    }
}
