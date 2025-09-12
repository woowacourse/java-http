package org.apache.tomcat.util.http.value;

public enum HttpHeader {
    LOCATION("location"),
    CONTENT_LENGTH("content-length"),
    SET_COOKIE("set-cookie"),
    COOKIE("cookie"),
    CONTENT_TYPE("content-type");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
