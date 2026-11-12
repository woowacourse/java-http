package org.apache.coyote.http11.request;

import java.util.Locale;

public enum HttpMethod {
    GET,
    POST,
    DELETE,
    PATCH,
    PUT;

    public static HttpMethod from(String value) {
        return valueOf(value.toUpperCase(Locale.ROOT));
    }
}
