package org.apache.coyote.http11.model;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    ;

    public boolean isGet() {
        return this.equals(GET);
    }

    public boolean isPost() {
        return this.equals(POST);
    }
}
