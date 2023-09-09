package org.apache.coyote.http;

import java.util.Arrays;
import java.util.Optional;

public enum HeaderKey {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    ;

    public final String value;

    HeaderKey(String value) {
        this.value = value;
    }

    public Optional<HeaderKey> from(String value) {
        return Arrays.stream(values())
                     .filter(headerKey -> headerKey.value.equals(value))
                     .findFirst();
    }
}
