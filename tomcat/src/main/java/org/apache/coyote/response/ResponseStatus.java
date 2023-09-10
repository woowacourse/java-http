package org.apache.coyote.response;

import static java.net.HttpURLConnection.*;

public enum ResponseStatus {
    OK(HTTP_OK, "OK"),
    UNAUTHORIZED(HTTP_UNAUTHORIZED, "Unauthorized"),
    MOVED_TEMP(HTTP_MOVED_TEMP, "Temporary Redirect"),
    INTERNET_SERVER(HTTP_INTERNAL_ERROR, "Internal Server Error"),
    NOT_FOUND(HTTP_NOT_FOUND, "Not Found");

    private final int code;
    private final String message;

    ResponseStatus(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return this.code + " " + this.message;
    }
}
