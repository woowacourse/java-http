package org.apache.coyote.http11.request;

enum HttpMethod {
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    POST,
    PUT,
    TRACE,
    CONNECT,
    ;


    public static HttpMethod parse(final String method) {
        for (final HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.name().equalsIgnoreCase(method)) {
                return httpMethod;
            }
        }
        throw new InvalidHttpRequestException("Invalid HTTP method: " + method);
    }

    public boolean equals(final String method) {
        return this.name().equalsIgnoreCase(method);
    }
}
