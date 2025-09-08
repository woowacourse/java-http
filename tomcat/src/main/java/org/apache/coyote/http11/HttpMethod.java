package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.exception.HttpStatusException;

public enum HttpMethod {

    GET,
    POST;

    public static HttpMethod from(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equals(method))
                .findFirst()
                .orElseThrow(() -> new HttpStatusException(HttpStatusCode.INTERNAL_SERVER_ERROR));
    }
}
