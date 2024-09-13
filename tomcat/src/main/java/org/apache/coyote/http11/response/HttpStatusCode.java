package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.Http11Processor.HTTP_VERSION;

public enum HttpStatusCode {

    OK(200, "OK"),
    REDIRECT(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error")
    ;

    private final int statusCode;
    private final String statusMessage;

    HttpStatusCode(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.statusMessage = reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public String toString() {
        return HTTP_VERSION + " " + statusCode + " " + statusMessage;
    }
}
