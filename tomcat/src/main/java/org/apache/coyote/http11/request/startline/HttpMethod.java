package org.apache.coyote.http11.request.startline;

import java.util.Arrays;
import org.apache.coyote.http11.exception.HttpStatusException;
import org.apache.coyote.http11.response.startline.HttpStatusCode;

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
