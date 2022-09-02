package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    GET;

    public static HttpMethod from(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메서드입니다. name = " + name));
    }
}
