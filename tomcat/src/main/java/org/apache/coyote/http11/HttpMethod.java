package org.apache.coyote.http11;

import java.util.Arrays;

/**
 * Enumerates HTTP methods. see <a href=https://datatracker.ietf.org/doc/html/rfc2616#section-5.1.1>RFC 2616, section 5.1.1</a>
 */
public enum HttpMethod {
    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT,
    ;

    public static HttpMethod from(String method) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.matches(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid HTTP method: " + method));
    }

    private boolean matches(String method) {
        return name().equalsIgnoreCase(method);
    }
}
