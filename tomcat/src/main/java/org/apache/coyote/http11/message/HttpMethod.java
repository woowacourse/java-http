package org.apache.coyote.http11.message;

public enum HttpMethod {
    GET,
    POST,
    ;

    public boolean isGet() {
        return this.equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return this.equals(HttpMethod.POST);
    }
}
