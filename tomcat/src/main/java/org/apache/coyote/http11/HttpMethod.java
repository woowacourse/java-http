package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST
    ;

    public static HttpMethod getHttpMethod(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.isMethod(name))
                .findAny()
                .orElseThrow();
    }

    public boolean isMethod(String name) {
        return name().equals(name);
    }
}
