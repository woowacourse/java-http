package org.apache.coyote.http11;

public enum HttpMethod {
    POST,
    GET,
    PUT,
    DELETE;

    public boolean isGet() {
        return this.equals(GET);
    }
}
