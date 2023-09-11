package org.apache.coyote.http11.message;

public enum HttpMethod {
    GET,
    POST;

    public HttpMethod findByName(final String name) {
        return HttpMethod.valueOf(name);
    }
}
