package org.apache.coyote.http11.response.header;

import static org.apache.coyote.http11.Constant.URI_SEPARATOR;

public enum Status {

    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    BAD_REQUEST(400, "BAD REQUEST"),
    UNSUPPORTED_METHOD(405, "METHOD NOT ALLOWED"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

    private static final String VERSION = "HTTP/1.1";

    private final int code;
    private final String message;

    Status(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return VERSION + URI_SEPARATOR +
                this.code + URI_SEPARATOR +
                this.message + URI_SEPARATOR;
    }
}
