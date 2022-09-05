package org.apache.coyote.http11.support;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import java.util.Arrays;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public static HttpHeader from(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findAny()
                .orElseThrow(InvalidHttpRequestException::new);
    }


    public static boolean contains(final String value) {
        return Arrays.stream(values())
                .anyMatch(it -> it.value.equals(value));
    }

    public String getValue() {
        return value;
    }
}
