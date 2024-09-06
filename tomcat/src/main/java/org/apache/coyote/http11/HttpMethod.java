package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    OPTIONS,
    ;

    public static HttpMethod from(String httpMethod) {
        if (httpMethod == null) {
            throw new IllegalArgumentException("HTTP Method cannot be null");
        }
        try {
            return valueOf(httpMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Not found HTTP Method : %s", httpMethod));
        }
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isNotGet() {
        return !isGet();
    }

    public boolean isPost() {
        return this == POST;
    }
}
