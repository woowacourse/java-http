package org.apache.coyote.http11;

public enum HttpMethod {

    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS;

    public boolean isGet() {
        return GET.equals(this);
    }
}
