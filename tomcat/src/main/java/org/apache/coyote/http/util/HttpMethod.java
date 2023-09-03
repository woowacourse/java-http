package org.apache.coyote.http.util;

import java.util.Arrays;
import org.apache.coyote.http.util.exception.UnsupportedHttpMethodException;

public enum HttpMethod {

    GET("get");

    private final String content;

    HttpMethod(final String content) {
        this.content = content;
    }

    public static HttpMethod findMethod(final String targetMethodName) {
        return Arrays.stream(HttpMethod.values())
                     .filter(httpMethod -> httpMethod.content.equalsIgnoreCase(targetMethodName))
                     .findAny()
                     .orElseThrow(UnsupportedHttpMethodException::new);
    }

    public boolean matches(final HttpMethod target) {
        return this == target;
    }
}
