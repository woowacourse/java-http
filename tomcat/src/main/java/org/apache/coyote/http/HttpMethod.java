package org.apache.coyote.http;

public enum HttpMethod implements HttpComponent {
    GET,
    POST,
    ;

    public static HttpMethod from(final String value) {
        return valueOf(value.toUpperCase());
    }

    @Override
    public String asString() {
        return name();
    }
}
