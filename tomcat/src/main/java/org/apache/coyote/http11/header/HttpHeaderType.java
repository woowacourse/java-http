package org.apache.coyote.http11.header;

import java.util.Arrays;

public enum HttpHeaderType {
    // request
    HOST("Host"),
    LOCATION("Location"),
    CONNECTION("Connection"),
    DATE("Date"),
    USER_AGENT("User-Agent"),
    FROM("From"),
    COOKIE("Cookie"),
    REFERER("Referer"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    AUTHORIZATION("Authorization"),
    ORIGIN("Origin"),
    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),

    CONTENT_TYPE("Content-Type"),
    CONTENT_ENCODING("Content-Language"),
    CONTENT_LOCATION("Content-Location"),
    CONTENT_DISPOSITION("Content-Disposition"),
    CONTENT_SECURITY_POLICY("Content-Security-Policy"),
    CONTENT_LANGUAGE("Content-Language"),
    CONTENT_LENGTH("Content-Length"),

    CACHE_CONTROL("Cache-Control"),
    LAST_MODIFIED("Last-Modified"),

    TRANSFER_ENCODING("Transfer-Encoding")
    ;

    private final String value;

    HttpHeaderType(final String value) {
        this.value = value;
    }

    public static String of(final String value) {
        return Arrays.stream(HttpHeaderType.values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .map(HttpHeaderType::getValue)
                .orElse(value);
    }

    public String getValue() {
        return value;
    }
}
