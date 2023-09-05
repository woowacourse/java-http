package org.apache.coyote.http11.common;

public enum HttpMethod {

    GET, POST;

    public boolean isEqualTo(HttpMethod httpMethod) {
        return this == httpMethod;
    }
}
