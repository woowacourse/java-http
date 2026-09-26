package org.apache.coyote.http11.request;

public enum HttpMethod {
    GET,
    POST,
    UNKNOWN;

    public static HttpMethod from(final String value) {
        for (HttpMethod method : values()) {
            if (method.name().equalsIgnoreCase(value)) {
                return method;
            }
        }

        return UNKNOWN;
    }
}
