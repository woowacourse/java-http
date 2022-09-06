package org.apache.coyote.support;

public enum HttpHeader {

    HTTP_1_1_STATUS("HTTP/1.1", "%s %s "),
    LOCATION("Location", "%s: %s "),
    CONTENT_TYPE("Content-Type", "%s: %s;charset=utf-8 "),
    CONTENT_LENGTH("Content-Length", "%s: %s ");

    private final String type;
    private final String format;

    HttpHeader(final String type, final String format) {
        this.type = type;
        this.format = format;
    }

    public String apply(String value) {
        return String.format(format, type, value);
    }

    public String apply(HttpStatus status) {
        return String.format(format, type, status.text());
    }
}
