package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Optional;

public enum HttpHeaderKey {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    CONNECTION("Connection"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    HOST("Host");

    private final String name;

    HttpHeaderKey(String name) {
        this.name = name;
    }

    public static Optional<HttpHeaderKey> findByName(String name) {
        return Arrays.stream(HttpHeaderKey.values())
                .filter(headerKey -> headerKey.name.equalsIgnoreCase(name))
                .findFirst();
    }

    public String getName() {
        return name;
    }
}
