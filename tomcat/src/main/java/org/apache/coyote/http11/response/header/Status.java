package org.apache.coyote.http11.response.header;

public enum Status {

    OK(200, "Ok"),
    NOT_FOUND(404, "Not Found"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    BAD_REQUEST(400, "Bad Request"),
    UNSUPPORTED_METHOD(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private static final String VERSION = "HTTP/1.1";

    private final int code;
    private final String message;

    Status(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return VERSION + " " + this.code + " " + this.message + " ";
    }
}
