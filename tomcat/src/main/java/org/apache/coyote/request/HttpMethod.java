package org.apache.coyote.request;

import java.util.Arrays;

import static java.util.Locale.ENGLISH;

public enum HttpMethod {

    GET, POST, PATCH, PUT, DELETE, OPTION;

    public static HttpMethod from(final String source) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.contains(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 HttpMethod 값입니다."));
    }

    private boolean contains(final String source) {
        return source.toUpperCase(ENGLISH)
                .contains(this.name());
    }
}
