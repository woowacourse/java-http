package org.apache.coyote.http11.request;

public enum HttpRequestMethod {

    GET,
    POST,
    ;

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
