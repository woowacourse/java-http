package org.apache.coyote.http11.request.requestline;

import java.util.Arrays;
import java.util.NoSuchElementException;


public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod from(String data) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(data.toUpperCase().strip()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(data + " does not exist"));
    }
}
