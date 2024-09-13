package org.apache.coyote.http;

public enum HttpMethod implements HttpComponent {
    GET,
    POST,
    ;

    public static HttpMethod from(final String value) {
        return valueOf(value.toUpperCase());
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }

    @Override
    public String asString() {
        return name();
    }
}
