package org.apache.coyote.support;

public enum HttpHeader {

    HTTP_1_1_STATUS("HTTP/1.1", "%s %s "),
    LOCATION("Location", "%s "),
    CONTENT_TYPE("Content-Type", "%s;charset=utf-8 "),
    CONTENT_LENGTH("Content-Length", "%s "),
    SET_COOKIE("Set-Cookie", "%s ");

    private final String type;
    private final String format;

    HttpHeader(final String type, final String format) {
        this.type = type;
        this.format = format;
    }

    public String apply(HttpStatus status) {
        return String.format(format, type, status.text());
    }

    public String apply(String value) {
        return String.format(format, value);
    }

    public String apply(final Session session) {
        return String.format(format, session.text());
    }

    public String apply(final HttpCookie cookie) {
        return String.format(format, cookie.value());
    }

    public String type() {
        return type;
    }

    public String format() {
        return format;
    }
}
