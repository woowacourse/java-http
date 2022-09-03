package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpMethod {
    GET("GET");

    private String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod from(final String value) {
        return Arrays.stream(HttpMethod.values())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 HttpHeaderType이 없습니다. " + value));
    }
}
