package org.apache.coyote.http11;

import java.util.Locale;

public enum HttpHeaderName {
    HOST("Host"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ALLOW("Allow"),
    LOCATION("Location");

    private final String value;
    private final String normalized;

    HttpHeaderName(final String value) {
        this.value = value;
        this.normalized = normalize(value);
    }

    public static String normalize(final String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public String getValue() {
        return value;
    }

    public String getNormalized() {
        return normalized;
    }
}
