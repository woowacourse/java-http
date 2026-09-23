package org.apache.coyote.http11;

public enum HttpMethod {

    GET,
    POST,
    PATCH,
    PUT,
    DELETE,
    UNKNOWN
    ;

    public static HttpMethod from(String value) {
        if (!value.matches("[!#$%&'*+.^_`|~0-9A-Za-z-]+")) {
            throw new IllegalArgumentException("Invalid HTTP method");
        }
        for (HttpMethod method : values()) {
            if (method.name().equals(value)) {
                return method;
            }
        }
        return UNKNOWN;
    }
}
