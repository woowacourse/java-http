package org.apache.coyote.http11.request.requestline;

import java.util.NoSuchElementException;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod from(String data) {
        if ("GET".equals(data.toUpperCase().strip())) {
            return GET;
        }
        if ("POST".equals(data.toUpperCase().strip())) {
            return POST;
        }
        if ("PUT".equals(data.toUpperCase().strip())) {
            return PUT;
        }
        if ("DELETE".equals(data.toUpperCase().strip())) {
            return DELETE;
        }
        throw new NoSuchElementException(data + " does not exist");
    }
}
