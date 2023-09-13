package org.apache.coyote.http11.response;

public enum StatusLine {
    OK("200 OK "),
    REDIRECT("302 Found "),
    NOT_FOUND("404 Not Found "),
    BAD_REQUEST("400 Bad Request "),
    INTERNAL_SERVER_ERROR("500 Internal Server Error ");

    private static final String PROTOCOL = "HTTP/1.1 ";

    private final String value;

    StatusLine(final String value) {
        this.value = value;
    }

    public String get() {
        return PROTOCOL + value;
    }

    public String getStatusCode() {
        return value.split(" ")[0];
    }
}
