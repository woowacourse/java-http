package org.apache.coyote.http11.request.startline;

import java.util.Arrays;
import org.apache.coyote.http11.exception.InternalServerErrorException;

public enum HttpMethod {

    GET,
    POST;

    public static HttpMethod from(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equals(method))
                .findFirst()
                .orElseThrow(InternalServerErrorException::new);
    }
}
