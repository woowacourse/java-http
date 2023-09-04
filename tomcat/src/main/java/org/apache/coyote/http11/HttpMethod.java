package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod from(String name) {
        return Arrays.stream(values())
            .filter(it -> it.name().equals(name))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

}
