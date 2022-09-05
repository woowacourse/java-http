package org.apache.coyote.http11.support;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import java.util.Arrays;

public enum HttpMethod {

    GET, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    public static HttpMethod from(final String method) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(method))
                .findAny()
                .orElseThrow(InvalidHttpRequestException::new);
    }
}
