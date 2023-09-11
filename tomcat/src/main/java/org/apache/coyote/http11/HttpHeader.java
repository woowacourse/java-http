package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie")
    ;

    private String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public static HttpHeader findByValue(String value) {
        return Arrays.stream(values())
                .filter(httpHeader -> httpHeader.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("invalid header value"));
    }

    public String getValue() {
        return value;
    }

}
