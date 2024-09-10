package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST
    ;

    public static HttpMethod getHttpMethod(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(name))
                .findAny()
                .orElseThrow();
    }

    public boolean isMethod(HttpMethod method) {
        return this.equals(method);
    }
}
