package org.apache.coyote.http11.request;

import java.util.Arrays;
import org.apache.coyote.http11.exception.InvalidMethodNameException;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH;

    public static HttpMethod find(final String methodName) {
        return Arrays.stream(values())
            .filter(method -> methodName.equalsIgnoreCase(method.name()))
            .findFirst()
            .orElseThrow(() -> new InvalidMethodNameException(methodName));
    }
}
