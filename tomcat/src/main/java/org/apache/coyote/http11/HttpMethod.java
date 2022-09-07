package org.apache.coyote.http11;

public enum HttpMethod {

    GET, POST;

    public static HttpMethod of(final String name) {
        return valueOf(name);
    }

    public boolean isSame(final HttpMethod httpMethod) {
        return this == httpMethod;
    }
}
