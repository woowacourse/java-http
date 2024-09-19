package org.apache.coyote.http11;

public enum HttpHeaderField {

    LOCATION("Location"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_TYPE("Content-Type")
    ;

    private final String headerField;

    HttpHeaderField(String headerField) {
        this.headerField = headerField;
    }

    public String getHeaderField() {
        return headerField;
    }
}
