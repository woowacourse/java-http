package org.apache.coyote.http11.request.header;

public enum Method {

    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT,
    PATCH,
    LINK,
    UNLINK,
    ;

    public static Method from(final String method) {
        try {
            return valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("존재하지 않는 HTTP Method 입니다. [%s]", method));
        }
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
