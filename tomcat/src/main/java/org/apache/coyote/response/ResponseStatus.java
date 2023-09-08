package org.apache.coyote.response;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;

public enum ResponseStatus {
    OK(HttpURLConnection.HTTP_OK, "OK"),
    UNAUTHORIZED(HttpsURLConnection.HTTP_UNAUTHORIZED, "Unauthorized"),
    MOVED_TEMP(HttpURLConnection.HTTP_MOVED_TEMP, "Temporary Redirect");

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
