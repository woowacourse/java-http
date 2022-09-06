package org.apache.coyote.support;

public enum HttpHeader {

    HTTP_1_1_STATUS_CODE("HTTP/1.1 %s "),
    LOCATION("Location: %s "),
    CONTENT_TYPE("Content-Type: %s;charset=utf-8 "),
    CONTENT_LENGTH("Content-Length: %s ");
    private final String format;

    HttpHeader(final String format) {
        this.format = format;
    }

    public String apply(String value) {
        return String.format(this.format, value);
    }
}
