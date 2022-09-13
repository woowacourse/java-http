package org.apache.coyote.support;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    ;

    public static final String DELIMITER = ": ";
    private static final String HEADER_LINE_FORMAT = "%s: %s ";

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toLineFormat(String value) {
        return String.format(HEADER_LINE_FORMAT, this.value, value);
    }
}
