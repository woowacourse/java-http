package org.apache.coyote.http11.common.request;

public enum HttpMethod {
    GET, POST, NONE;

    public static HttpMethod from(final String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
