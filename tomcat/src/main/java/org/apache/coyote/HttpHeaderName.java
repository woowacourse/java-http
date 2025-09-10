package org.apache.coyote;

public enum HttpHeaderName {

    CONTENT_TYPE("content-type"),
    CONTENT_LENGTH("content-length"),
    COOKIE("cookie"),
    LOCATION("location"),
    ;

    private final String value;

    HttpHeaderName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
