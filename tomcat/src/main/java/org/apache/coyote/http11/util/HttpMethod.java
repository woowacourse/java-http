package org.apache.coyote.http11.util;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET", false),
    POST("POST", true),
    ;

    private final String name;
    private final boolean hasBody;

    HttpMethod(final String name, final boolean hasBody) {
        this.name = name;
        this.hasBody = hasBody;
    }

    public static boolean hasBody(final String name) {
        return Arrays.stream(HttpMethod.values())
                     .filter(method -> method.name.equals(name))
                     .map(httpMethod -> httpMethod.hasBody)
                     .findAny()
                     .orElseThrow(() -> new IllegalArgumentException("잘못된 Http Mehod명입니다. " + name));
    }

    public boolean isSameMethod(final String method) {
        return this.name.equals(method);
    }
}
