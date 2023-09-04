package org.apache.coyote.http11;

public enum HttpMethod {

    GET, POST;

    public boolean isEqualTo(HttpMethod httpMethod) {
        return this == httpMethod;
    }
}
