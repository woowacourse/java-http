package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod from(final String value) {
        try {
            return HttpMethod.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 HTTP Method: " + value, e);
        }
    }
}
