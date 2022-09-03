package org.apache.coyote.http11;

public enum HttpMethod {

    GET;

    public static HttpMethod of(final String name) {
        return valueOf(name);
    }
}
